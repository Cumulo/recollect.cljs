
(ns recollect.comp.panel
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]))

(defn on-click [op] (fn [e dispatch!] (dispatch! op (rand-int 100))))

(defn render-button [title op]
  (div {:style ui/button, :attrs {:inner-text title}, :event {:click (on-click op)}}))

(def style-line {:height "40px"})

(defn render []
  (fn [state mutate!]
    (div
     {}
     (div
      {:style style-line}
      (render-button "Change lit-0" :lit-0)
      (comp-space 8 nil)
      (render-button "Change lit-1" :lit-1))
     (div
      {:style style-line}
      (render-button "Change map-0" :map-0)
      (comp-space 8 nil)
      (render-button "Remove map-0" :map-0-rm))
     (div
      {:style style-line}
      (render-button "Change vec-0" :vec-0)
      (comp-space 8 nil)
      (render-button "Remove vec-0" :vec-0-rm))
     (div
      {:style style-line}
      (render-button "Change seq-0" :seq-0)
      (comp-space 8 nil)
      (render-button "Change seq-0 remove" :seq-0-rm))
     (div
      {:style style-line}
      (render-button "Change set-0" :set-0)
      (comp-space 8 0)
      (render-button "Change set-0 remove" :set-0-rm))
     (div {:style style-line} (render-button "Change date" :date))
     (div {:style style-line} (render-button "Change types" :types)))))

(def comp-panel (create-comp :panel render))
