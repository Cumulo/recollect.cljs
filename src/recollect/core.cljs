
(ns recollect.core
  (:require [recollect.types :refer [Piece piece?]] [recollect.util :refer [=seq]]))

(declare render-map)

(declare render-view)

(declare render-vector)

(declare render-set)

(declare render-seq)

(defn create-piece [piece-name renderer]
  (fn [piece-name & args] (Piece. piece-name args (apply renderer args) renderer)))

(defn render-seq [data-tree cached] (->> data-tree (map (fn [x] (render-view x nil)))))

(defn render-set [data-tree cached]
  (->> data-tree (map (fn [x] (render-view x nil))) (into #{})))

(defn render-vector [data-tree cached]
  (->> data-tree (map-indexed (fn [idx x] (render-view x (get cached idx)))) (into [])))

(defn render-view [data-tree cached-data-tree]
  (println "Calling render-view:" data-tree)
  (if (= (type data-tree) (type cached-data-tree))
    (cond
      (piece? data-tree)
        (if (and (identical? (:name data-tree) (:name cached-data-tree))
                 (identical? (:render data-tree) (:render cached-data-tree))
                 (=seq (:args data-tree) (:args cached-data-tree)))
          cached-data-tree
          (assoc data-tree :data (render-view (:data data-tree) (:data cached-data-tree))))
      (or (nil? data-tree) (number? data-tree) (string? data-tree) (keyword? data-tree))
        data-tree
      (map? data-tree) (render-map data-tree cached-data-tree)
      (vector? data-tree) (render-vector data-tree cached-data-tree)
      (seq? data-tree) (render-seq data-tree cached-data-tree)
      (set? data-tree) (render-set data-tree cached-data-tree)
      :else (do (println "Unexpected data:" data-tree) nil))
    (cond
      (piece? data-tree) (assoc data-tree :data (render-view (:data data-tree) nil))
      (or (nil? data-tree) (number? data-tree) (string? data-tree) (keyword? data-tree))
        data-tree
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
