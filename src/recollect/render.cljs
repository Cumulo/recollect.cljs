
(ns recollect.render
  (:require [respo.render.html :refer [make-string]]
            [shell-page.core :refer [make-page spit slurp]]
            [recollect.comp.container :refer [comp-container]]
            [recollect.schema :as schema]))

(def base-info
  {:title "Recollect",
   :icon "http://cdn.tiye.me/logo/cumulo.png",
   :ssr nil,
   :inline-styles [(slurp "entry/main.css")]})

(defn dev-page []
  (make-page
   ""
   (merge
    base-info
    {:styles ["http://localhost:8100/main.css"],
     :scripts ["/browser/lib.js" "/browser/main.js"]})))

(def preview? (= "preview" js/process.env.prod))

(defn prod-page []
  (let [html-content (make-string (comp-container nil schema/store))
        cljs-info (.parse js/JSON (slurp "dist/cljs-manifest.json"))
        cdn (if preview? "" "http://cdn.tiye.me/recollect/")
        prefix-cdn (fn [x] (str cdn x))]
    (make-page
     html-content
     (merge
      base-info
      {:styles ["http://cdn.tiye.me/favored-fonts/main.css"],
       :scripts (map
                 prefix-cdn
                 [(-> cljs-info (aget 0) (aget "js-name"))
                  (-> cljs-info (aget 1) (aget "js-name"))])}))))

(defn main! []
  (if (= js/process.env.env "dev")
    (spit "target/index.html" (dev-page))
    (spit "dist/index.html" (prod-page))))
