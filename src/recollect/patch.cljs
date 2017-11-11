
(ns recollect.patch
  (:require [clojure.set :refer [union difference]] [recollect.schema :as schema]))

(defn patch-map-set [base coord data] (if (empty? coord) data (assoc-in base coord data)))

(defn patch-vector-drop [base coord data]
  (update-in base coord (fn [cursor] (subvec cursor 0 data))))

(defn patch-vector-append [base coord data]
  (update-in base coord (fn [cursor] (into [] (concat cursor data)))))

(defn patch-seq [base coord data]
  (let [[n content] data]
    (update-in
     base
     coord
     (fn [cursor] (concat content (if (zero? n) cursor (drop n cursor)))))))

(defn patch-set [base coord data]
  (let [[removed added] data]
    (if (empty? coord)
      (-> base (difference removed) (union added))
      (update-in base coord (fn [cursor] (-> cursor (difference removed) (union added)))))))

(defn patch-map-remove [base coord path]
  (if (empty? coord)
    (dissoc base path)
    (update-in base coord (fn [cursor] (dissoc cursor path)))))

(defn patch-one [base change]
  (let [[op coord data] change]
    (cond
      (= op schema/tree-op-vec-append) (patch-vector-append base coord data)
      (= op schema/tree-op-vec-drop) (patch-vector-drop base coord data)
      (= op schema/tree-op-dissoc) (patch-map-remove base coord data)
      (= op schema/tree-op-assoc) (patch-map-set base coord data)
      (= op schema/tree-op-set-splice) (patch-set base coord data)
      (= op schema/tree-op-seq-splice) (patch-seq base coord data)
      :else (do (println "Unkown op:" op) base))))

(defn patch-bunch [base changes]
  (if (empty? changes) base (recur (patch-one base (first changes)) (rest changes))))
