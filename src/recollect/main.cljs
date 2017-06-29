
(ns recollect.main
  (:require [respo.core :refer [render! clear-cache! realize-ssr!]]
            [respo.cursor :refer [mutate]]
            [recollect.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [recollect.bunch :refer [render-bunch]]
            [recollect.types :refer [conceal-twig]]
            [recollect.twig.container :refer [twig-container]]
            [recollect.diff :refer [diff-bunch]]
            [recollect.patch :refer [patch-bunch]]
            [recollect.updater :refer [updater]]
            [recollect.schema :as schema]))

(def ssr? (some? (.querySelector js/document "meta.respo-ssr")))

(defonce *store
  (atom
   (merge
    schema/store
    {:lit-0 1,
     :vec-0 [{:a 1}],
     :seq-0 (list {:a 1}),
     :set-0 #{{:a 1}},
     :map-0 {:x 0},
     :in-map {:lit-1 1, :vec-1 [{:a 1}]},
     :date {:year 2016, :month 10},
     :user {:name "Chen"},
     :types {:name 1, "name" 2}})))

(defn dispatch! [op op-data]
  (let [new-store (if (= op :states)
                    (update @*store :states (mutate op-data))
                    (updater @*store op op-data))]
    (reset! *store new-store)))

(defonce *data-bunch (atom nil))

(defonce *client-store (atom schema/store))

(defn render-data-bunch! []
  (let [data-bunch (render-bunch (twig-container @*store) @*data-bunch)
        *changes (atom [])
        collect! (fn [x] (swap! *changes conj x))]
    (diff-bunch collect! [] @*data-bunch data-bunch)
    (comment println "Data bunch:" (conceal-twig data-bunch))
    (println "Changes:" @*changes)
    (reset! *data-bunch data-bunch)
    (let [new-client (patch-bunch @*client-store @*changes)]
      (comment println "After patching:" new-client)
      (reset! *client-store new-client))))

(def mount-target (.querySelector js/document ".app"))

(defn render-app! [renderer]
  (renderer mount-target (comp-container @*data-bunch @*client-store) dispatch!))

(defn main! []
  (if ssr? (render-app! realize-ssr!))
  (render-app! render!)
  (add-watch *store :changes render-data-bunch!)
  (add-watch *client-store :changes (fn [] (render-app! render!)))
  (render-data-bunch!)
  (println "app started!"))

(defn reload! [] (clear-cache!) (render-data-bunch!) (println "code update."))

(set! (.-onload js/window) main!)
