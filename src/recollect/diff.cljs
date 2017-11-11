
(ns recollect.diff
  (:require [recollect.util :refer [literal? =seq compare-more]]
            [recollect.types :refer [twig? conceal-twig]]
            [clojure.set :refer [difference]]
            [recollect.schema :as schema]))

(declare diff-map)

(declare find-vector-changes)

(declare diff-vector)

(declare find-map-changes)

(declare diff-bunch)

(defn diff-set [collect! coord a b]
  (comment assert (or (coll? a) (coll? b)) "[Recollect] sets to diff should hold literals")
  (let [added (difference b a), removed (difference a b)]
    (collect! [schema/tree-op-set-splice coord [removed added]])))

(defn find-seq-changes [collect! coord ra rb options]
  (cond
    (and (empty? ra) (empty? rb)) nil
    (empty? ra) (collect! [schema/tree-op-seq-splice coord [0 (conceal-twig (reverse rb))]])
    (empty? rb) (collect! [schema/tree-op-seq-splice coord [(count ra) []]])
    :else
      (if (identical? (first ra) (first rb))
        (recur collect! coord (rest ra) (rest rb) options)
        (collect!
         [schema/tree-op-seq-splice coord [(count ra) (conceal-twig (reverse rb))]]))))

(defn diff-seq [collect! coord a b options]
  (find-seq-changes collect! coord (reverse a) (reverse b) options))

(defn by-key [x y] (compare-more (first x) (first y)))

(defn diff-bunch
  ([a b options]
   (let [*changes (atom []), collect! (fn [x] (swap! *changes conj x))]
     (diff-bunch collect! [] a b options)
     @*changes))
  ([collect! coord a b options]
   (if (= (type a) (type b))
     (cond
       (twig? a)
         (if (not (identical? a b)) (recur collect! coord (:data a) (:data b) options))
       (literal? b) (if (not (identical? a b)) (collect! [schema/tree-op-assoc coord b]))
       (map? b) (diff-map collect! coord a b options)
       (set? b) (diff-set collect! coord a b)
       (vector? b) (diff-vector collect! coord a b options)
       (seq? b) (diff-seq collect! coord a b options)
       :else (do (println "Unexpected data:" a b)))
     (collect! [schema/tree-op-assoc coord (conceal-twig b)]))))

(defn find-map-changes [collect! coord a-pairs b-pairs options]
  (let [[ka va] (first a-pairs), [kb vb] (first b-pairs)]
    (cond
      (and (empty? a-pairs) (empty? b-pairs)) nil
      (empty? a-pairs)
        (do
         (collect! [schema/tree-op-assoc (conj coord kb) (conceal-twig vb)])
         (recur collect! coord [] (rest b-pairs) options))
      (empty? b-pairs)
        (do
         (collect! [schema/tree-op-dissoc coord ka])
         (recur collect! coord (rest a-pairs) [] options))
      (= -1 (compare-more ka kb))
        (do
         (collect! [schema/tree-op-dissoc coord ka])
         (recur collect! coord (rest a-pairs) b-pairs options))
      (= 1 (compare-more ka kb))
        (do
         (collect! [schema/tree-op-assoc (conj coord kb) (conceal-twig vb)])
         (recur collect! coord a-pairs (rest b-pairs) options))
      :else
        (do
         (diff-bunch collect! (conj coord ka) va vb options)
         (recur collect! coord (rest a-pairs) (rest b-pairs) options)))))

(defn diff-vector [collect! coord a b options]
  (find-vector-changes collect! 0 coord a b options))

(defn find-vector-changes [collect! idx coord a-pairs b-pairs options]
  (comment println idx a-pairs b-pairs)
  (cond
    (and (empty? a-pairs) (empty? b-pairs)) nil
    (empty? b-pairs) (collect! [schema/tree-op-vec-drop coord idx])
    (empty? a-pairs) (collect! [schema/tree-op-vec-append coord (conceal-twig b-pairs)])
    :else
      (do
       (diff-bunch collect! (conj coord idx) (first a-pairs) (first b-pairs) options)
       (recur collect! (inc idx) coord (rest a-pairs) (rest b-pairs) options))))

(defn diff-map [collect! coord a b options]
  (let [a-pairs (sort by-key a), b-pairs (sort by-key b), k (:key options)]
    (if (not= (get a k) (get b k))
      (collect! [schema/tree-op-assoc coord (conceal-twig b)])
      (find-map-changes collect! coord a-pairs b-pairs options))))
