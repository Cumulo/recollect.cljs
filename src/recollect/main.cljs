
(ns recollect.main
  (:require [respo.core
             :refer
             [render! clear-cache! falsify-stage! render-element gc-states!]]
            [recollect.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [recollect.bunch :refer [render-bunch]]
            [recollect.types :refer [conceal-twig]]
            [recollect.twig.container :refer [twig-container]]
            [recollect.diff :refer [diff-bunch]]
            [recollect.patch :refer [patch-bunch]]
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
    :types {:name 1, "name" 2},
    :lit-0 1,
    :user {:name "Chen"}}))

(defn dispatch! [op op-data]
  (let [new-store (updater @store-ref op op-data)] (reset! store-ref new-store)))

(defonce data-bunch-ref (atom nil))

(defn render-data-bunch! []
  (let [data-bunch (render-bunch (twig-container @store-ref) @data-bunch-ref)
        changes-ref (atom [])
        collect! (fn [x] (swap! changes-ref conj x))]
    (diff-bunch collect! [] @data-bunch-ref data-bunch)
    (comment println "Data bunch:" (conceal-twig data-bunch))
    (println "Changes:" @changes-ref)
    (reset! data-bunch-ref data-bunch)
    (let [new-client (patch-bunch @client-store-ref @changes-ref)]
      (comment println "After patching:" new-client)
      (reset! client-store-ref new-client))))

(defonce states-ref (atom {}))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @data-bunch-ref @client-store-ref) target dispatch! states-ref)))

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
       (render-element (comp-container @data-bunch-ref @client-store-ref) states-ref)
       dispatch!)))
  (render-app!)
  (add-watch store-ref :gc (fn [] (gc-states! states-ref)))
  (add-watch store-ref :changes render-data-bunch!)
  (add-watch client-store-ref :changes render-app!)
  (add-watch states-ref :changes render-app!)
  (render-data-bunch!)
  (println "app started!"))

(defn on-jsload! [] (clear-cache!) (render-data-bunch!) (println "code update."))

(set! (.-onload js/window) -main!)
