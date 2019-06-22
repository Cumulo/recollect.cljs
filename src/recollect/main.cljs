
(ns recollect.main
  (:require [respo.core :refer [render! clear-cache! realize-ssr!]]
            [respo.cursor :refer [mutate]]
            [recollect.comp.container :refer [comp-container]]
            [cljs.reader :refer [read-string]]
            [recollect.twig :refer [render-twig]]
            [recollect.types :refer [conceal-twig]]
            [recollect.twig.container :refer [twig-container]]
            [recollect.diff :refer [diff-twig]]
            [recollect.patch :refer [patch-twig]]
            [recollect.updater :refer [updater]]
            [recollect.schema :as schema]
            [recollect.config :as config]))

(defonce *client-store (atom schema/store))

(defonce *data-twig (atom nil))

(defonce *store
  (atom
   (merge
    schema/store
    {:lit-0 1,
     :vec-0 [{:a 1}],
     :seq-0 (list {:a 1}),
     :set-0 #{1 :a},
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

(def mount-target (.querySelector js/document ".app"))

(defn render-app! [renderer]
  (renderer mount-target (comp-container @*data-twig @*client-store) dispatch!))

(defn render-data-twig! []
  (let [data-twig (render-twig (twig-container @*store) @*data-twig)
        options {:key :id}
        changes (diff-twig @*data-twig data-twig options)]
    (comment println "Data twig:" (conceal-twig data-twig))
    (println "Changes:" changes)
    (reset! *data-twig data-twig)
    (let [new-client (patch-twig @*client-store changes)]
      (comment println "After patching:" new-client)
      (reset! *client-store new-client))))

(def ssr? (some? (.querySelector js/document "meta.respo-ssr")))

(defn main! []
  (println "Running mode:" (if config/dev? "dev" "release"))
  (if ssr? (render-app! realize-ssr!))
  (render-app! render!)
  (add-watch *store :changes render-data-twig!)
  (add-watch *client-store :changes (fn [] (render-app! render!)))
  (render-data-twig!)
  (println "app started!"))

(defn reload! [] (clear-cache!) (render-data-twig!) (println "code update."))
