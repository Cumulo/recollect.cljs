
(ns recollect.twig.card (:require [recollect.twig :refer [deftwig]]))

(deftwig twig-card (user date) {:user user, :date date})
