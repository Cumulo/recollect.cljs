
(ns recollect.core )

(defn render-view [data-tree cached-data-tree] data-tree)

(def cached-data-view-ref (atom nil))

(defn render-collection! [data-tree]
  (let [data-view (render-view data-tree @cached-data-view-ref)]
    (reset! cached-data-view-ref data-view)))
