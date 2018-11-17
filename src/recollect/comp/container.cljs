
(ns recollect.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core :refer [defcomp cursor-> <> span div]]
            [respo.comp.space :refer [=<]]
            [recollect.comp.panel :refer [comp-panel]]
            [respo-value.comp.value :refer [comp-value]]))

(defcomp
 comp-container
 (data-twig client-store)
 (let [states (:states client-store)]
   (div
    {:style (merge ui/global)}
    (comp-panel)
    (comment div {} (<> span (pr-str data-twig) nil))
    (div {} (<> span (pr-str client-store) nil))
    (cursor-> :value comp-value states client-store 0))))
