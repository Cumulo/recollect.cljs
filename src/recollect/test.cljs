
(ns recollect.test
  (:require [cljs.test :refer [deftest is run-tests]]
            [recollect.diff :refer [diff-bunch]]
            [recollect.schema :as schema]))

(deftest
 diff-map-same-id
 ()
 (let [a {:id 1, :data 1}, b {:id 1, :data 2}, options {:key :id}]
   (is (= (diff-bunch a b options) [[[:data] schema/tree-op-assoc 2]]))))

(deftest
 diff-map-by-ids
 ()
 (let [a {:id 1, :data 1}, b {:id 2, :data 1}, options {:key :id}]
   (is (= (diff-bunch a b options) [[[] schema/tree-op-assoc {:id 2, :data 1}]]))))

(defn main! [] (println "Test loaded!") (run-tests))

(defn reload! [] (println "Code updated!") (run-tests))
