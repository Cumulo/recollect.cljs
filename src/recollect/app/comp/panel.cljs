
(ns recollect.app.comp.panel
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core :refer [defcomp <> span div]]
            [respo.comp.space :refer [=<]]))

(defn on-click [op] (fn [e dispatch!] (dispatch! op (rand-int 100))))

(defn render-button [title op]
  (div {:style ui/button, :inner-text title, :on-click (on-click op)}))

(def style-line {:height "40px"})

(defcomp
 comp-panel
 ()
 (div
  {}
  (div
   {:style style-line}
   (render-button "Change lit-0" :lit-0)
   (=< 8 nil)
   (render-button "Change lit-1" :lit-1))
  (div
   {:style style-line}
   (render-button "Change map-0" :map-0)
   (=< 8 nil)
   (render-button "Remove map-0" :map-0-rm))
  (div
   {:style style-line}
   (render-button "Change vec-0" :vec-0)
   (=< 8 nil)
   (render-button "Remove vec-0" :vec-0-rm))
  (div
   {:style style-line}
   (render-button "Change seq-0" :seq-0)
   (=< 8 nil)
   (render-button "Change seq-0 remove" :seq-0-rm))
  (div
   {:style style-line}
   (render-button "Change set-0" :set-0)
   (=< 8 0)
   (render-button "Change set-0 remove" :set-0-rm))
  (div {:style style-line} (render-button "Change date" :date))
  (div {:style style-line} (render-button "Change types" :types))))
