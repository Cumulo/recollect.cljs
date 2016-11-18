
(ns recollect.diff
  (:require [recollect.util :refer [literal? =seq]]
            [recollect.types :refer [branch? conceal-branch]]))

(declare diff-view)

(declare diff-map)

(declare find-vector-changes)

(declare diff-vector)

(declare find-map-changes)

(defn find-set-changes [acc coord a b]
  (cond
    (and (empty? a) (empty? b)) acc
    (empty? a)
      (recur (conj acc [coord :st/+ (conceal-branch (first b))]) coord (list) (rest b))
    (empty? b)
      (recur (conj acc [coord :st/- (conceal-branch (first a))]) coord (rest a) (list))
    (= -1 (compare (first a) (first b)))
      (recur (conj acc [coord :st/- (conceal-branch (first a))]) coord (rest a) (list))
    (= 1 (compare (first a) (first b)))
      (recur (conj acc [coord :st/+ (conceal-branch (first b))]) coord (list) (rest b))
    :else (recur acc coord (rest a) (rest b))))

(defn diff-set [coord a b]
  (let [sorted-a (into (sorted-set) a), sorted-b (into (sorted-set) b)]
    (find-set-changes [] coord sorted-a sorted-b)))

(def no-changes [])

(defn diff-seq [coord a b] (if (=seq a b) no-changes [coord :m/! (conceal-branch b)]))

(defn find-map-changes [acc coord a-pairs b-pairs]
  (let [[ka va] (first a-pairs), [kb vb] (first b-pairs)]
    (cond
      (and (empty? a-pairs) (empty? b-pairs)) acc
      (empty? a-pairs)
        (let [next-acc (conj acc [(conj coord kb) :m/+ (conceal-branch vb)])]
          (recur next-acc coord [] (rest b-pairs)))
      (empty? b-pairs)
        (let [next-acc (conj acc [[conj coord ka] :m/- nil])]
          (recur next-acc coord [] (rest a-pairs)))
      (= -1 (compare ka kb))
        (recur (conj acc [[conj coord ka] :m/- nil]) coord (rest a-pairs) b-pairs)
      (= 1 (compare ka kb))
        (recur
         (conj acc [conj coord kb] :m/+ (conceal-branch vb))
         coord
         a-pairs
         (rest b-pairs))
      :else
        (recur
         (conj acc (diff-view (conj coord ka) va vb))
         coord
         (rest a-pairs)
         (rest b-pairs)))))

(defn diff-vector [coord a b]
  (let [a-pairs (sort-by first a), b-pairs (sort-by first b)]
    (find-vector-changes [] 0 coord a-pairs b-pairs)))

(defn find-vector-changes [acc idx coord a-pairs b-pairs]
  (cond
    (and (empty? a-pairs) (empty? b-pairs)) acc
    (empty? b-pairs)
      (recur
       (conj acc [coord :v/+ [idx (conceal-branch (first a-pairs))]])
       (inc idx)
       coord
       (rest a-pairs)
       [])
    (empty? a-pairs) (recur (conj acc [coord :v/- idx]) (inc idx) coord [] (rest b-pairs))))

(defn diff-map [coord a b]
  (let [a-pairs (sort-by first a), b-pairs (sort-by first b)]
    (find-map-changes [] coord a-pairs b-pairs)))

(defn diff-view [coord a b]
  (if (= (type a) (type b))
    (cond
      (branch? a) (if (identical? a b) no-changes (diff-view coord (:data a) (:data b)))
      (literal? b) (if (identical? a b) no-changes [[coord :m/! b]])
      (map? b) (diff-map coord a b)
      (set? b) (diff-set coord a b)
      (vector? b) (diff-vector coord a b)
      (seq? b) (diff-seq coord a b)
      :else (do (println "Unexpected data" a b) []))
    [[coord :m/! (conceal-branch b)]]))
