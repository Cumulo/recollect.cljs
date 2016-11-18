
(ns recollect.piece.container (:require [recollect.core :refer [create-piece]]))

(defn render [store] (assoc store :demo "nothing"))

(def piece-container (create-piece :container render))
