
(ns recollect.updater )

(defn updater [store op op-data]
  (case op
    :lit-0 (assoc store :lit-0 op-data)
    :lit-1 (assoc-in store [:in-map :lit-1] op-data)
    :map-0 (assoc-in store [:map-0 :y] op-data)
    :vec-0 (update store :vec-0 (fn [vec-0] (conj vec-0 op-data)))
    (do (println "Unhandled op:" op) store)))
