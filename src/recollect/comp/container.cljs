
(ns recollect.comp.container
  (:require-macros [respo.macros :refer [defcomp <> span div]])
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.core :refer [create-comp]]
            [respo.comp.space :refer [=<]]
            [recollect.comp.panel :refer [comp-panel]]
            [respo-value.comp.value :refer [render-value]]))

(defcomp
 comp-container
 (data-bunch client-store)
 (let [states (:states client-store)]
   (div
    {:style (merge ui/global)}
    (comp-panel)
    (comment div {} (<> span (pr-str data-bunch) nil))
    (div {} (<> span (pr-str client-store) nil))
    (render-value states client-store))))
