
(ns recollect.types
  (:require [cljs.reader :refer [register-tag-parser! read-string]]
            [clojure.string :as string]))

(defn record->name [record-name] (string/replace (pr-str record-name) "/" "."))

(defrecord Piece [name args data render])

(defn piece? [x] (= (type x) Piece))

(register-tag-parser! (record->name Piece) map->Piece)
