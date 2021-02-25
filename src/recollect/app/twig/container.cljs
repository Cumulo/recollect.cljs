
(ns recollect.app.twig.container (:require [recollect.app.twig.card :refer [twig-card]]))

(defn twig-container [store] (merge store {:card (twig-card (:user store) (:date store))}))
