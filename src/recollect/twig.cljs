
(ns recollect.twig (:require [caches.core :as caches]) (:require-macros [recollect.twig]))

(defn call-twig-func [f params]
  (let [xs (concat [f] params), v (caches/access-cache xs)]
    (if (some? v) v (let [result (apply f params)] (caches/write-cache! xs result) result))))

(defn clear-twig-caches! [] (caches/reset-caches!))

(defn new-twig-loop! [] (caches/new-loop!))
