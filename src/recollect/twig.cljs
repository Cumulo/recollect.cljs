
(ns recollect.twig
  (:require [recollect.types :refer [Twig twig?]] [recollect.util :refer [=seq literal?]]))

(declare render-map)

(declare render-seq)

(declare render-set)

(declare render-twig)

(declare render-vector)

(defn create-twig [twig-name renderer]
  (fn [& args] (Twig. twig-name args (apply renderer args) renderer)))

(defn render-vector [data-tree cached]
  (->> data-tree (map-indexed (fn [idx x] (render-twig x (get cached idx)))) (into [])))

(defn render-twig [data-tree cached-data-tree]
  (comment println "Calling render-twig:" data-tree cached-data-tree)
  (if (= (type data-tree) (type cached-data-tree))
    (cond
      (twig? data-tree)
        (if (and (identical? (:name data-tree) (:name cached-data-tree))
                 (identical? (:render data-tree) (:render cached-data-tree))
                 (=seq (:args data-tree) (:args cached-data-tree)))
          (do cached-data-tree)
          (assoc data-tree :data (render-twig (:data data-tree) (:data cached-data-tree))))
      (literal? data-tree) data-tree
      (map? data-tree) (render-map data-tree cached-data-tree)
      (vector? data-tree) (render-vector data-tree cached-data-tree)
      (seq? data-tree) (render-seq data-tree cached-data-tree)
      (set? data-tree) (render-set data-tree cached-data-tree)
      :else (do (println "Unexpected data:" data-tree) nil))
    (cond
      (twig? data-tree) (assoc data-tree :data (render-twig (:data data-tree) nil))
      (literal? data-tree) data-tree
      (map? data-tree) (render-map data-tree nil)
      (vector? data-tree) (render-vector data-tree nil)
      (seq? data-tree) (render-seq data-tree nil)
      (set? data-tree) (render-set data-tree nil)
      :else (do (println "Unexpected data:" data-tree) nil))))

(defn render-set [data-tree cached]
  (->> data-tree (map (fn [x] (render-twig x nil))) (into #{})))

(defn render-seq [data-tree cached]
  (let [size (count data-tree), cached-list (into [] cached), length (count cached-list)]
    (->> data-tree
         (map-indexed
          (fn [idx x] (render-twig x (get cached-list (- length (- size idx)))))))))

(defn render-map [data-tree cached]
  (->> data-tree
       (map (fn [entry] (let [[k v] entry] [k (render-twig v (get cached k))])))
       (into {})))
