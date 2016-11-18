
(ns recollect.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]))

(defn render [data-view client-store]
  (fn [state mutate!]
    (div
     {:style (merge ui/global)}
     (div {} (comp-text (pr-str data-view) nil))
     (div {} (comp-text (pr-str client-store) nil)))))

(def comp-container (create-comp :container render))
