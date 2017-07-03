
(ns recollect.diff
  (:require [recollect.util :refer [literal? =seq compare-more]]
            [recollect.types :refer [twig? conceal-twig]]
            [clojure.set :refer [difference]]))

(declare diff-map)

(declare find-vector-changes)

(declare diff-vector)

(declare find-map-changes)

(declare diff-bunch)

(defn diff-set [collect! coord a b]
  (let [added (difference b a), removed (difference a b)]
    (collect! [coord :st/-+ [removed added]])))

(defn find-seq-changes [collect! coord ra rb]
  (cond
    (and (empty? ra) (empty? rb)) nil
    (empty? ra) (collect! [coord :sq/-+ [0 (conceal-twig (reverse rb))]])
    (empty? rb) (collect! [coord :sq/-+ [(count ra) []]])
    :else
      (if (identical? (first ra) (first rb))
        (recur collect! coord (rest ra) (rest rb))
        (collect! [coord :sq/-+ [(count ra) (conceal-twig (reverse rb))]]))))

(defn diff-seq [collect! coord a b]
  (find-seq-changes collect! coord (reverse a) (reverse b)))

(defn by-key [x y] (compare-more (first x) (first y)))

(defn diff-bunch
  ([a b]
   (let [*changes (atom []), collect! (fn [x] (swap! *changes conj x))]
     (diff-bunch collect! [] a b)
     @*changes))
  ([collect! coord a b]
   (if (= (type a) (type b))
     (cond
       (twig? a) (if (not (identical? a b)) (diff-bunch collect! coord (:data a) (:data b)))
       (literal? b) (if (not (identical? a b)) (collect! [coord :m/! b]))
       (map? b) (diff-map collect! coord a b)
       (set? b) (diff-set collect! coord a b)
       (vector? b) (diff-vector collect! coord a b)
       (seq? b) (diff-seq collect! coord a b)
       :else (do (println "Unexpected data:" a b)))
     (collect! [coord :m/! (conceal-twig b)]))))

(defn find-map-changes [collect! coord a-pairs b-pairs]
  (let [[ka va] (first a-pairs), [kb vb] (first b-pairs)]
    (cond
      (and (empty? a-pairs) (empty? b-pairs)) nil
      (empty? a-pairs)
        (do
         (collect! [(conj coord kb) :m/! (conceal-twig vb)])
         (recur collect! coord [] (rest b-pairs)))
      (empty? b-pairs)
        (do (collect! [coord :m/- ka]) (recur collect! coord (rest a-pairs) []))
      (= -1 (compare-more ka kb))
        (do (collect! [coord :m/- ka]) (recur collect! coord (rest a-pairs) b-pairs))
      (= 1 (compare-more ka kb))
        (do
         (collect! [(conj coord kb) :m/! (conceal-twig vb)])
         (recur collect! coord a-pairs (rest b-pairs)))
      :else
        (do
         (diff-bunch collect! (conj coord ka) va vb)
         (recur collect! coord (rest a-pairs) (rest b-pairs))))))

(defn diff-vector [collect! coord a b] (find-vector-changes collect! 0 coord a b))

(defn find-vector-changes [collect! idx coord a-pairs b-pairs]
  (comment println idx a-pairs b-pairs)
  (cond
    (and (empty? a-pairs) (empty? b-pairs)) nil
    (empty? b-pairs) (collect! [coord :v/-! idx])
    (empty? a-pairs) (collect! [coord :v/+! (conceal-twig b-pairs)])
    :else
      (do
       (diff-bunch collect! (conj coord idx) (first a-pairs) (first b-pairs))
       (recur collect! (inc idx) coord (rest a-pairs) (rest b-pairs)))))

(defn diff-map [collect! coord a b]
  (let [a-pairs (sort by-key a), b-pairs (sort by-key b)]
    (find-map-changes collect! coord a-pairs b-pairs)))
