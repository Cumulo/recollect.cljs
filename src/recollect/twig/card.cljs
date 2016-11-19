
(ns recollect.twig.card (:require [recollect.bunch :refer [create-twig]]))

(defn render [user date] {:date date, :user user})

(def twig-card (create-twig :card render))
