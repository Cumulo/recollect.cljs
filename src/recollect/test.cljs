
(ns recollect.test
  (:require [cljs.test :refer [deftest is run-tests]]
            [recollect.diff :refer [diff-twig]]
            [recollect.patch :refer [patch-twig]]
            [recollect.schema :as schema]
            [recollect.util :refer [vec-add seq-add]]))

(deftest
 test-vec-add
 ()
 (let [a [1 2 3 4], b [5 6 7 8]] (is (= (vec-add a b) [1 2 3 4 5 6 7 8]))))

(deftest
 test-seq-add
 ()
 (let [a (list 1 2 3 4), b (list 5 6 7 8)] (is (= (seq-add a b) (list 1 2 3 4 5 6 7 8)))))

(deftest
 test-diff-vectors
 ()
 (let [a {:a [1 2 3 4]}
       b {:a [1 6 7 8]}
       options {:key :id}
       changes [[schema/tree-op-assoc [:a 1] 6]
                [schema/tree-op-assoc [:a 2] 7]
                [schema/tree-op-assoc [:a 3] 8]]]
   (is (= changes (diff-twig a b options)))
   (is (= b (patch-twig a changes)))))

(deftest
 test-diff-maps
 ()
 (let [a {:a {:b 1}}
       b {:a {:c 2}}
       options {:key :id}
       changes [[schema/tree-op-dissoc [:a] :b] [schema/tree-op-assoc [:a :c] 2]]]
   (is (= changes (diff-twig a b options)))
   (is (= b (patch-twig a changes)))))

(deftest
 test-diff-sets
 ()
 (let [a {:a #{1 2 3}}
       b {:a #{2 3 4}}
       options {:key :id}
       changes [[schema/tree-op-set-splice [:a] [#{1} #{4}]]]]
   (is (= changes (diff-twig a b options)))
   (is (= b (patch-twig a changes)))))

(deftest
 test-diff-map-by-ids
 ()
 (let [a {:id 1, :data 1}
       b {:id 2, :data 1}
       options {:key :id}
       changes [[schema/tree-op-assoc [] {:id 2, :data 1}]]]
   (is (= changes (diff-twig a b options)))
   (is (= b (patch-twig a changes)))))

(deftest
 test-diff-map-same-id
 ()
 (let [a {:id 1, :data 1}
       b {:id 1, :data 2}
       options {:key :id}
       changes [[schema/tree-op-assoc [:data] 2]]]
   (is (= changes (diff-twig a b options)))
   (is (= b (patch-twig a changes)))))

(defn main! [] (println "Test loade!") (run-tests))
