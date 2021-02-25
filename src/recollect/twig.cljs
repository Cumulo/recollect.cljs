
(ns recollect.twig
  (:require [memof.core :as memof]
            [memof.alias
             :refer
             [*memof-call-states reset-calling-caches! tick-calling-loop!]]))

(defn clear-twig-caches! [] (reset-calling-caches!))

(defn new-twig-loop! [] (tick-calling-loop!))

(defn show-twig-summay [] (memof/show-summary @*memof-call-states))
