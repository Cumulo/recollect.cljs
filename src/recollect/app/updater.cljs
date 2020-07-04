
(ns recollect.app.updater (:require [respo.cursor :refer [update-states]]))

(defn updater [store op op-data]
  (case op
    :states (update-states store op-data)
    :lit-0 (assoc store :lit-0 op-data)
    :lit-1 (assoc-in store [:in-map :lit-1] op-data)
    :map-0 (assoc-in store [:map-0 :y] op-data)
    :map-0-rm (update-in store [:map-0] (fn [cursor] (dissoc cursor :y)))
    :vec-0 (update store :vec-0 (fn [vec-0] (conj vec-0 op-data :cursor)))
    :vec-0-rm (update store :vec-0 (fn [vec-0] (into [] (butlast vec-0))))
    :seq-0 (update store :seq-0 (fn [seq-0] (cons op-data seq-0)))
    :seq-0-rm (update store :seq-0 (fn [seq-0] (rest seq-0)))
    :set-0 (update store :set-0 (fn [set-0] (conj set-0 op-data)))
    :set-0-rm (update store :set-0 (fn [set-0] (into #{} (rest set-0))))
    :date (update-in store [:date :month] inc)
    :types (update store :types (fn [types-map] (assoc types-map op-data true)))
    (do (println "Unhandled op:" op) store)))
