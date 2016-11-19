
(ns recollect.diff
  (:require [recollect.util :refer [literal? =seq]]
            [recollect.types :refer [branch? conceal-branch]]
            [clojure.set :refer [difference]]))

(declare diff-view)

(declare diff-map)

(declare find-vector-changes)

(declare diff-vector)

(declare find-map-changes)

(def no-changes [])

(defn diff-set [coord a b]
  (let [added (difference b a), removed (difference a b)]
    (into
     []
     (concat
      (if (empty? added) no-changes [[coord :st/++ (conceal-branch added)]])
      (if (empty? removed) no-changes [[coord :st/-- (conceal-branch removed)]])))))

(defn find-seq-changes [coord ra rb]
  (cond
    (and (empty? ra) (empty? rb)) []
    (empty? ra) [[coord :sq/-+ [0 (conceal-branch (reverse rb))]]]
    (empty? rb) [[coord :sq/-+ [(count ra) []]]]
    :else
      (if (identical? (first ra) (first rb))
        (recur coord (rest ra) (rest rb))
        [[coord :sq/-+ [(count ra) (conceal-branch (reverse rb))]]])))

(defn diff-seq [coord a b] (find-seq-changes coord (reverse a) (reverse b)))

(defn find-map-changes [acc coord a-pairs b-pairs]
  (let [[ka va] (first a-pairs), [kb vb] (first b-pairs)]
    (cond
      (and (empty? a-pairs) (empty? b-pairs)) acc
      (empty? a-pairs)
        (let [next-acc (conj acc [(conj coord kb) :m/! (conceal-branch vb)])]
          (recur next-acc coord [] (rest b-pairs)))
      (empty? b-pairs)
        (let [next-acc (conj acc [coord :m/- ka])] (recur next-acc coord [] (rest a-pairs)))
      (= -1 (compare ka kb))
        (recur (conj acc [(conj coord ka) :m/- nil]) coord (rest a-pairs) b-pairs)
      (= 1 (compare ka kb))
        (recur
         (conj acc [(conj coord kb) :m/! (conceal-branch vb)])
         coord
         a-pairs
         (rest b-pairs))
      :else
        (recur
         (into [] (concat acc (diff-view (conj coord ka) va vb)))
         coord
         (rest a-pairs)
         (rest b-pairs)))))

(defn diff-vector [coord a b] (find-vector-changes [] 0 coord a b))

(defn find-vector-changes [acc idx coord a-pairs b-pairs]
  (comment println idx a-pairs b-pairs)
  (cond
    (and (empty? a-pairs) (empty? b-pairs)) acc
    (empty? b-pairs) (conj acc [coord :v/-! idx])
    (empty? a-pairs) (conj acc [coord :v/+! (conceal-branch b-pairs)])
    :else
      (recur
       (into [] (concat acc (diff-view (conj coord idx) (first a-pairs) (first b-pairs))))
       (inc idx)
       coord
       (rest a-pairs)
       (rest b-pairs))))

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
