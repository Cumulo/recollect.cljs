
(ns recollect.twig.card (:require [recollect.bunch :refer [create-twig]]))

(defn render [user date] {:user user, :date date})

(def twig-card (create-twig :card render))
