
(ns recollect.twig.container
  (:require [recollect.twig.card :refer [twig-card]] [recollect.twig :refer [deftwig]]))

(deftwig
 twig-container
 (store)
 (merge store {:card (twig-card (:user store) (:date store))}))
