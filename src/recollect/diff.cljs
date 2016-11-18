
(ns recollect.diff
  (:require [recollect.util :refer [literal?]] [recollect.types :refer [piece?]]))

(declare diff-view)

(declare diff-map)

(declare find-vector-changes)

(declare diff-vector)

(declare find-map-changes)

(defn diff-set [coord a b] [coord :replace b])

(def no-changes [])

(defn find-map-changes [acc coord a-pairs b-pairs]
  (let [[ka va] (first a-pairs), [kb vb] (first b-pairs)]
    (cond
      (and (empty? a-pairs) (empty? b-pairs)) acc
      (empty? a-pairs)
        (let [next-acc (conj acc [(conj coord kb) :add vb])]
          (recur next-acc coord [] (rest b-pairs)))
      (empty? b-pairs)
        (let [next-acc (conj acc [conj coord ka] :rm)]
          (recur next-acc coord [] (rest a-pairs)))
      (= -1 (compare ka kb))
        (recur (conj acc [conj coord ka] :rm) coord (rest a-pairs) b-pairs)
      (= 1 (compare ka kb))
        (recur (conj acc [conj coord kb] :add vb) coord a-pairs (rest b-pairs))
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
       (conj acc [coord :insert [idx (first a-pairs)]])
       (inc idx)
       coord
       (rest a-pairs)
       [])
    (empty? a-pairs)
      (recur (conj acc [coord :remove idx]) (inc idx) coord [] (rest b-pairs))))

(defn diff-map [coord a b]
  (let [a-pairs (sort-by first a), b-pairs (sort-by first b)]
    (find-map-changes [] coord a-pairs b-pairs)))

(defn diff-view [coord a b]
  (if (= (type a) (type b))
    (cond
      (piece? a) (if (identical? a b) no-changes (diff-view coord (:data a) (:data b)))
      (literal? b) (if (identical? a b) no-changes [[coord :replace b]])
      (map? b) (diff-map coord a b)
      (set? b) (diff-set coord a b)
      (vector? b) (diff-vector coord a b)
      (seq? b) nil
      :else (do (println "Unexpected data" a b) []))
    [[coord :replace b]]))
