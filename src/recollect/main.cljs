
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
            [recollect.patch :refer [patch-view]]))

(defonce client-store-ref (atom nil))

(defn dispatch! [op op-data] )

(defonce data-view-ref (atom nil))

(defonce store-ref
  (atom {:groups {0 {:title "demo", :tasks {0 {:done? false, :title "demo"}}}}}))

(defn render-data-view! []
  (let [data-view (render-view (branch-container @store-ref) @data-view-ref)
        changes (diff-view [] @data-view-ref data-view)
        new-client (patch-view @client-store-ref changes)]
    (println "Changes:" changes new-client)
    (reset! data-view-ref data-view)
    (reset! client-store-ref (conceal-branch data-view))))

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
