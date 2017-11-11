
(ns recollect.test
  (:require [cljs.test :refer [deftest is run-tests]]
            [recollect.diff :refer [diff-bunch]]
            [recollect.schema :as schema]
            [recollect.util :refer [vec-add seq-add]]))

(deftest
 diff-map-same-id
 ()
 (let [a {:id 1, :data 1}, b {:id 1, :data 2}, options {:key :id}]
   (is (= (diff-bunch a b options) [[schema/tree-op-assoc [:data] 2]]))))

(deftest
 diff-map-by-ids
 ()
 (let [a {:id 1, :data 1}, b {:id 2, :data 1}, options {:key :id}]
   (is (= (diff-bunch a b options) [[schema/tree-op-assoc [] {:id 2, :data 1}]]))))

(deftest
 test-vec-add
 ()
 (let [a (list 1 2 3 4), b (list 5 6 7 8)] (is (= (seq-add a b) (list 1 2 3 4 5 6 7 8)))))

(defn main! [] (println "Test loade!") (run-tests))
