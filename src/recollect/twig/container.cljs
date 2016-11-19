
(ns recollect.twig.container
  (:require [recollect.bunch :refer [create-twig]] [recollect.twig.card :refer [twig-card]]))

(defn render [store] (merge store {:card (twig-card (:user store) (:date store))}))

(def twig-container (create-twig :container render))
