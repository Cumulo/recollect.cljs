
(ns recollect.patch (:require [clojure.set :refer [union difference]]))

(defn patch-map-remove [base coord path]
  (update-in base coord (fn [cursor] (dissoc cursor path))))

(defn patch-set-add [base coord data]
  (update-in base coord (fn [cursor] (union cursor data))))

(defn patch-vector-append [base coord data]
  (update-in base coord (fn [cursor] (into [] (concat cursor data)))))

(defn patch-map-set [base coord data] (if (empty? coord) data (assoc-in base coord data)))

(defn patch-vector-drop [base coord data]
  (update-in base coord (fn [cursor] (subvec cursor 0 data))))

(defn patch-set-remove [base coord data]
  (update-in base coord (fn [cursor] (difference cursor data))))

(defn patch-one [base change]
  (let [[coord op data] change]
    (case op
      :v/+! (patch-vector-append base coord data)
      :v/-! (patch-vector-drop base coord data)
      :m/- (patch-map-remove base coord data)
      :m/! (patch-map-set base coord data)
      :st/++ (patch-set-add base coord data)
      :st/-- (patch-set-remove base coord data)
      (do (println "Unkown op:" op) base))))

(defn patch-view [base changes]
  (if (empty? changes) base (recur (patch-one base (first changes)) (rest changes))))
