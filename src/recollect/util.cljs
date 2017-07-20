
(ns recollect.util )

(defn type->int [x]
  (cond
    (number? x) 0
    (keyword? x) 1
    (string? x) 2
    (nil? x) 3
    :else (throw (js/Error. (str "Failed to compare, it's: " (pr-str x))))))

(defn literal? [x] (not (coll? x)))

(defn =seq [xs ys]
  (if (empty? xs)
    (if (empty? ys) true false)
    (if (empty? ys)
      false
      (if (identical? (first xs) (first ys)) (recur (rest xs) (rest ys)) false))))

(defn compare-more [x y]
  (let [type-x (type->int x), type-y (type->int y)]
    (if (= type-x type-y) (compare x y) (compare type-x type-y))))
