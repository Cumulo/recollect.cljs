
(ns recollect.diff (:require [recollect.util :refer [literal?]]))

(def no-changes [])

(defn diff-view [coord a b]
  (if (= (type a) (type b))
    (cond
      (literal? b) (if (identical? a b) no-changes [[coord :replace b]])
      (map? b) nil
      (set? b) nil
      (vector? b) nil
      (seq? b) nil
      :else (do (println "Unexpected data" a b) []))
    [[coord :replace b]]))
