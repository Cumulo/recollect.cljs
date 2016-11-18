
(ns recollect.branch.container (:require [recollect.core :refer [create-branch]]))

(defn render [store] (assoc store :demo "nothing"))

(def branch-container (create-branch :container render))
