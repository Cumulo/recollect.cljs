
(ns recollect.comp.panel
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]))

(def style-line {:height "40px"})

(defn on-click [op] (fn [e dispatch!] (dispatch! op (rand-int 100))))

(defn render-button [title op]
  (div {:style ui/button, :event {:click (on-click op)}, :attrs {:inner-text title}}))

(defn render []
  (fn [state mutate!]
    (div
     {}
     (div
      {:style style-line}
      (render-button "Change lit-0" :lit-0)
      (comp-space 8 nil)
      (render-button "Change lit-1" :lit-1))
     (div {:style style-line} (render-button "Change map-0" :map-0))
     (div {:style style-line} (render-button "Change vec-0" :vec-0))
     (div {:style style-line} (render-button "Change seq-0" :seq-0))
     (div {:style style-line} (render-button "Change set-0" :set-0))
     (div {:style style-line} (render-button "Change date" :date)))))

(def comp-panel (create-comp :panel render))
