
(ns recollect.twig.card
  (:require [recollect.bunch :refer [create-twig]] [recollect.macros :refer [deftwig]]))

(deftwig twig-card (user date) {:user user, :date date})
