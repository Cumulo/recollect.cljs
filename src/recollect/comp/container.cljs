
(ns recollect.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]))

(defn render [data-view]
  (fn [state mutate!] (div {:style (merge ui/global)} (comp-text (pr-str data-view) nil))))

(def comp-container (create-comp :container render))
