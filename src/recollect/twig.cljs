
(ns recollect.twig (:require [caches.core :as caches]) (:require-macros [recollect.twig]))

(defonce *twig-caches (caches/new-caches {}))

(defn call-twig-func [f params]
  (let [xs (concat [f] params), v (caches/access-cache *twig-caches xs)]
    (if (some? v)
      v
      (let [result (apply f params)] (caches/write-cache! *twig-caches xs result) result))))

(defn clear-twig-caches! [] (caches/reset-caches! *twig-caches))

(defn new-twig-loop! [] (caches/new-loop! *twig-caches))

(defn show-twig-summay [] (caches/show-summary! *twig-caches))
