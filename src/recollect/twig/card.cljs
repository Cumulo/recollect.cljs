
(ns recollect.twig.card (:require [recollect.macros :refer [deftwig]]))

(deftwig twig-card (user date) {:user user, :date date})
