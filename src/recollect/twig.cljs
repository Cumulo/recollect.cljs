
(ns recollect.twig (:require [memof.core :as memof]) (:require-macros [recollect.twig]))

(defonce *twig-caches (atom (memof/new-states {})))

(defn call-twig-func [f params]
  (let [v (memof/access-record *twig-caches f params)]
    (if (some? v)
      v
      (let [result (apply f params)]
        (memof/write-record! *twig-caches f params result)
        result))))

(defn clear-twig-caches! [] (memof/reset-entries! *twig-caches))

(defn new-twig-loop! [] (memof/new-loop! *twig-caches))

(defn show-twig-summay [] (memof/show-summary @*twig-caches))
