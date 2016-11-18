
(ns recollect.types
  (:require [cljs.reader :refer [register-tag-parser! read-string]]
            [clojure.string :as string]
            [recollect.util :refer [literal?]]))

(defrecord Piece [name args data render])

(defn piece? [x] (= (type x) Piece))

(defn conceal-piece [data]
  (cond
    (literal? data) data
    (piece? data) (:data (conceal-piece data))
    (map? data)
      (->> data (map (fn [entry] (let [[k v] entry] [k (conceal-piece v)]))) (into {}))
    (vector? data) (mapv conceal-piece data)
    (seq? data) (map conceal-piece data)
    (set? data) (->> data (map conceal-piece) (into #{}))
    :else (do (println "Unkown data to conceal-piece:" data) data)))

(defn record->name [record-name] (string/replace (pr-str record-name) "/" "."))

(register-tag-parser! (record->name Piece) map->Piece)
