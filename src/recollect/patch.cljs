
(ns recollect.patch (:require [clojure.set :refer [union difference]]))

(defn patch-map-remove [base coord path]
  (if (empty? coord)
    (dissoc base path)
    (update-in base coord (fn [cursor] (dissoc cursor path)))))

(defn patch-vector-append [base coord data]
  (update-in base coord (fn [cursor] (into [] (concat cursor data)))))

(defn patch-map-set [base coord data] (if (empty? coord) data (assoc-in base coord data)))

(defn patch-set [base coord data]
  (let [[removed added] data]
    (if (empty? coord)
      (-> base (difference removed) (union added))
      (update-in base coord (fn [cursor] (-> cursor (difference removed) (union added)))))))

(defn patch-vector-drop [base coord data]
  (update-in base coord (fn [cursor] (subvec cursor 0 data))))

(defn patch-seq [base coord data]
  (let [[n content] data]
    (update-in
     base
     coord
     (fn [cursor] (concat content (if (zero? n) cursor (drop n cursor)))))))

(defn patch-one [base change]
  (let [[coord op data] change]
    (case op
      :v/+! (patch-vector-append base coord data)
      :v/-! (patch-vector-drop base coord data)
      :m/- (patch-map-remove base coord data)
      :m/! (patch-map-set base coord data)
      :st/-+ (patch-set base coord data)
      :sq/-+ (patch-seq base coord data)
      (do (println "Unkown op:" op) base))))

(defn patch-bunch [base changes]
  (if (empty? changes) base (recur (patch-one base (first changes)) (rest changes))))
