
(ns recollect.test
  (:require [cljs.test :refer [deftest is run-tests]] [recollect.diff :refer [diff-bunch]]))

(deftest
 diff-map-same-id
 ()
 (let [a {:id 1, :data 1}, b {:id 1, :data 2}, options {:key :id}]
   (is (= (diff-bunch a b options) [[[:data] :m/! 2]]))))

(deftest
 diff-map-by-ids
 ()
 (let [a {:id 1, :data 1}, b {:id 2, :data 1}, options {:key :id}]
   (is (= (diff-bunch a b options) [[[] :m/! {:id 2, :data 1}]]))))

(defn main! [] (println "Test loaded!") (run-tests))

(defn reload! [] (println "Code updated!") (run-tests))
