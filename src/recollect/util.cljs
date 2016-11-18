
(ns recollect.util )

(defn =seq [xs ys]
  (if (empty? xs)
    (if (empty? ys) true false)
    (if (empty? ys)
      false
      (if (identical? (first xs) (first ys)) (recur (rest xs) (rest ys)) false))))

(defn literal? [x]
  (or (number? x) (string? x) (keyword? x) (nil? x) (symbol? x) (true? x) (false? x)))
