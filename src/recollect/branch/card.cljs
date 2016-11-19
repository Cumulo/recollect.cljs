
(ns recollect.branch.card (:require [recollect.core :refer [create-branch]]))

(defn render [user date] {:date date, :user user})

(def branch-card (create-branch :card render))
