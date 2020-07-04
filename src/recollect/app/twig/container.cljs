
(ns recollect.app.twig.container
  (:require [recollect.app.twig.card :refer [twig-card]] [recollect.twig :refer [deftwig]]))

(deftwig
 twig-container
 (store)
 (merge store {:card (twig-card (:user store) (:date store))}))
