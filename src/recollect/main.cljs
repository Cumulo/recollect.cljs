
(ns recollect.main
  (:require [respo.core
             :refer
             [render! clear-cache! falsify-stage! render-element gc-states!]]
            [recollect.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [recollect.core :refer [render-view]]
            [recollect.types :refer [conceal-branch]]
            [recollect.branch.container :refer [branch-container]]
            [recollect.diff :refer [diff-view]]
            [recollect.patch :refer [patch-view]]
            [recollect.updater :refer [updater]]))

(defonce client-store-ref (atom nil))

(defonce store-ref
  (atom
   {:seq-0 (list {:a 1}),
    :vec-0 [{:a 1}],
    :map-0 {:x 0},
    :date {:month 10, :year 2016},
    :set-0 #{{:a 1}},
    :in-map {:vec-1 [{:a 1}], :lit-1 1},
    :lit-0 1,
    :user {:name "Chen"}}))

(defn dispatch! [op op-data]
  (let [new-store (updater @store-ref op op-data)] (reset! store-ref new-store)))

(defonce data-view-ref (atom nil))

(defn render-data-view! []
  (let [data-view (render-view (branch-container @store-ref) @data-view-ref)
        changes (diff-view [] @data-view-ref data-view)
        new-client (patch-view @client-store-ref changes)]
    (comment println "Data view:" (conceal-branch data-view))
    (println "Changes:" changes)
    (comment println "After patching:" new-client)
    (reset! data-view-ref data-view)
    (reset! client-store-ref new-client)))

(defonce states-ref (atom {}))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @data-view-ref @client-store-ref) target dispatch! states-ref)))

(def ssr-stages
  (let [ssr-element (.querySelector js/document "#ssr-stages")
        ssr-markup (.getAttribute ssr-element "content")]
    (read-string ssr-markup)))

(defn -main! []
  (enable-console-print!)
  (if (not (empty? ssr-stages))
    (let [target (.querySelector js/document "#app")]
      (falsify-stage!
       target
       (render-element (comp-container @data-view-ref @client-store-ref) states-ref)
       dispatch!)))
  (render-app!)
  (add-watch store-ref :gc (fn [] (gc-states! states-ref)))
  (add-watch store-ref :changes render-data-view!)
  (add-watch client-store-ref :changes render-app!)
  (add-watch states-ref :changes render-app!)
  (render-data-view!)
  (println "app started!"))

(defn on-jsload! [] (clear-cache!) (render-data-view!) (println "code update."))

(set! (.-onload js/window) -main!)
