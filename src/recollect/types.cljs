
(ns recollect.types
  (:require [cljs.reader :refer [register-tag-parser! read-string]]
            [clojure.string :as string]
            [recollect.util :refer [literal?]]))

(defrecord Branch [name args data render])

(defn branch? [x] (= (type x) Branch))

(defn conceal-branch [data]
  (cond
    (literal? data) data
    (branch? data) (:data (conceal-branch data))
    (map? data)
      (->> data (map (fn [entry] (let [[k v] entry] [k (conceal-branch v)]))) (into {}))
    (vector? data) (mapv conceal-branch data)
    (seq? data) (map conceal-branch data)
    (set? data) (->> data (map conceal-branch) (into #{}))
    :else (do (println "Unkown data to conceal-branch:" data) data)))

(defn record->name [record-name] (string/replace (pr-str record-name) "/" "."))

(register-tag-parser! (record->name Branch) map->Branch)
