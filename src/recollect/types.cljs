
(ns recollect.types
  (:require [cljs.reader :refer [register-tag-parser! read-string]]
            [clojure.string :as string]
            [recollect.util :refer [literal?]]))

(defrecord Twig [name args data render])

(defn twig? [x] (= (type x) Twig))

(defn conceal-twig [data]
  (comment println "conceal" data)
  (cond
    (literal? data) data
    (twig? data) (conceal-twig (:data data))
    (map? data)
      (->> data (map (fn [entry] (let [[k v] entry] [k (conceal-twig v)]))) (into {}))
    (vector? data) (mapv conceal-twig data)
    (seq? data) (map conceal-twig data)
    (set? data) (->> data (map conceal-twig) (into #{}))
    :else (do (println "Unkown data to conceal-twig:" data) data)))

(defn record->name [record-name] (string/replace (pr-str record-name) "/" "."))

(register-tag-parser! (record->name Twig) map->Twig)
