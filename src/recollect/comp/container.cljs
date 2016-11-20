
(ns recollect.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [recollect.comp.panel :refer [comp-panel]]
            [respo-value.comp.value :refer [render-value]]))

(defn render [data-bunch client-store]
  (fn [state mutate!]
    (div
     {:style (merge ui/global)}
     (comp-panel)
     (comment div {} (comp-text (pr-str data-bunch) nil))
     (div {} (comp-text (pr-str client-store) nil))
     (render-value client-store))))

(def comp-container (create-comp :container render))
