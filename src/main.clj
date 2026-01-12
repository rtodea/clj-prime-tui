(ns main
  (:require [lanterna.terminal :as t]))

;; 1. The pure functional logic
(defn is-prime? [n]
  (cond
    (< n 2) false
    (= n 2) true
    (even? n) false
    :else
    (empty?
      (filter
        #(zero? (mod n %))
        (range
          3
          (inc
            (Math/sqrt n))
          2)))))

(defn primes-upto [limit]
  (filter is-prime? (range limit)))

(comment
  (primes-upto 20)
  )

;; 2. The TUI: Display logic

(defn draw-ui [term limit primes]
  (t/clear term)

  ;; Draw Header
  (t/move-cursor term 2 1)
  (t/put-string term (str "PRIME GENERATOR (Limit: " limit ")"))
  (t/move-cursor term 2 2)
  (t/put-string term "-------------------------------")

  ;; Draw Primes
  (doseq [[i p] (map-indexed vector primes)]
    (let [row (+ 4 (quot i 5))    ;; 5 items per row
          col (+ 2 (* (mod i 5) 8))] ;; Spacing of 8
      (t/move-cursor term col row)
      (t/put-string term (str p))))

  ;; Draw Footer
  (t/move-cursor term 2 20)
  (t/put-string term "Press 'q' to quit, 'up'/'down' to change limit."))

(defn start-app []
  (let [term (t/get-terminal :text)] ;; :text mode for standard terminal
    (t/start term)
    (t/get-key-blocking term) ;; Wait for initial input to ensure ready

    (loop [limit 50]
      (let [primes (primes-upto limit)]
        (draw-ui term limit primes)

        ;; Wait for user input
        (let [input (t/get-key-blocking term)]
          (case input
            \q (t/stop term)
            :arrow-up (recur (+ limit 10))
            :arrow-down (recur (max 2 (- limit 10)))
            (recur limit)))))))

(defn -main []
  (start-app))

(comment
(start-app)

  )
