
(ns recollect.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [recollect.comp.panel :refer [comp-panel]]
            [respo-value.comp.value :refer [render-value]]))

(defn render [data-view client-store]
  (fn [state mutate!]
    (div
     {:style (merge ui/global)}
     (comment div {} (comp-text (pr-str data-view) nil))
     (div {} (comp-text (pr-str client-store) nil))
     (comp-panel)
     (render-value client-store))))

(def comp-container (create-comp :container render))
