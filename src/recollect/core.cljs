
(ns recollect.core (:require [recollect.types :refer [Piece]]))

(defn create-piece [piece-name renderer]
  (fn [& args] (Piece. args renderer (apply renderer args))))

(defn render-view [data-tree cached-data-tree] data-tree)

(def cached-data-view-ref (atom nil))

(defn render-collection! [data-tree]
  (let [data-view (render-view data-tree @cached-data-view-ref)]
    (reset! cached-data-view-ref data-view)))
