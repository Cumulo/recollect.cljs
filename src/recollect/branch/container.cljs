
(ns recollect.branch.container
  (:require [recollect.core :refer [create-branch]]
            [recollect.branch.card :refer [branch-card]]))

(defn render [store] (merge store {:card (branch-card (:user store) (:date store))}))

(def branch-container (create-branch :container render))
