
(ns recollect.core
  (:require [recollect.types :refer [Branch branch?]] [recollect.util :refer [=seq literal?]]))

(declare render-map)

(declare render-view)

(declare render-vector)

(declare render-set)

(declare render-seq)

(defn render-seq [data-tree cached]
  (let [size (count data-tree), cached-list (into [] cached), length (count cached-list)]
    (->> data-tree
         (map-indexed
          (fn [idx x] (render-view x (get cached-list (- length (- size idx)))))))))

(defn render-set [data-tree cached]
  (->> data-tree (map (fn [x] (render-view x nil))) (into #{})))

(defn render-vector [data-tree cached]
  (->> data-tree (map-indexed (fn [idx x] (render-view x (get cached idx)))) (into [])))

(defn render-view [data-tree cached-data-tree]
  (println "Calling render-view:" data-tree cached-data-tree)
  (if (= (type data-tree) (type cached-data-tree))
    (cond
      (branch? data-tree)
        (if (and (identical? (:name data-tree) (:name cached-data-tree))
                 (identical? (:render data-tree) (:render cached-data-tree))
                 (=seq (:args data-tree) (:args cached-data-tree)))
          cached-data-tree
          (assoc data-tree :data (render-view (:data data-tree) (:data cached-data-tree))))
      (literal? data-tree) data-tree
      (map? data-tree) (render-map data-tree cached-data-tree)
      (vector? data-tree) (render-vector data-tree cached-data-tree)
      (seq? data-tree) (render-seq data-tree cached-data-tree)
      (set? data-tree) (render-set data-tree cached-data-tree)
      :else (do (println "Unexpected data:" data-tree) nil))
    (cond
      (branch? data-tree) (assoc data-tree :data (render-view (:data data-tree) nil))
      (literal? data-tree) data-tree
      (map? data-tree) (render-map data-tree nil)
      (vector? data-tree) (render-vector data-tree nil)
      (seq? data-tree) (render-seq data-tree nil)
      (set? data-tree) (render-set data-tree nil)
      :else (do (println "Unexpected data:" data-tree) nil))))

(defn render-map [data-tree cached]
  (->> data-tree
       (map (fn [entry] (let [[k v] entry] [k (render-view v (get cached k))])))
       (into {})))

(def cached-data-view-ref (atom nil))

(defn create-branch [branch-name renderer]
  (fn [& args] (Branch. branch-name args (apply renderer args) renderer)))
