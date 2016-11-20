
(set-env!
  :resource-paths #{"polyfill"}
  :dependencies '[[org.clojure/clojure       "1.8.0"       :scope "test"]
                  [org.clojure/clojurescript "1.9.293"     :scope "test"]
                  [adzerk/boot-cljs          "1.7.228-1"   :scope "test"]
                  [adzerk/boot-reload        "0.4.12"      :scope "test"]
                  [cirru/boot-stack-server   "0.1.23"      :scope "test"]
                  [adzerk/boot-test          "1.1.2"       :scope "test"]
                  [mvc-works/hsl             "0.1.2"       :scope "test"]
                  [respo/ui                  "0.1.4"       :scope "test"]
                  [respo/value               "0.1.7"       :scope "test"]
                  [respo                     "0.3.32"      :scope "test"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]]
         '[stack-server.core  :refer [start-stack-editor! transform-stack]]
         '[respo.alias        :refer [html head title script style meta' div link body]]
         '[respo.render.html  :refer [make-html]]
         '[adzerk.boot-test   :refer :all]
         '[clojure.java.io    :as    io])

(def +version+ "0.1.0")

(task-options!
  pom {:project     'cumulo/recollect
       :version     +version+
       :description "Cached rendering and diff/patch library designed for Cumulo project."
       :url         "https://github.com/Cumulo/recollect"
       :scm         {:url "https://github.com/Cumulo/recollect"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(defn use-text [x] {:attrs {:innerHTML x}})
(defn html-dsl [data fileset]
  (make-html
    (html {}
      (head {}
        (title (use-text "Recollect"))
        (link {:attrs {:rel "icon" :type "image/png" :href "http://logo.mvc-works.org/mvc.png"}})
        (link {:attrs {:rel "stylesheet" :href "style.css"}})
        (meta'{:attrs {:charset "utf-8"}})
        (meta' {:attrs {:name "viewport" :content "width=device-width, initial-scale=1"}})
        (meta' {:attrs {:id "ssr-stages" :content "#{}"}})
        (style (use-text "body {margin: 0;}"))
        (style (use-text "body * {box-sizing: border-box;}"))
        (script {:attrs {:id "config" :type "text/edn" :innerHTML (pr-str data)}}))
      (body {}
        (div {:attrs {:id "app"}})
        (script {:attrs {:src "main.js"}})))))

(deftask html-file
  "task to generate HTML file"
  [d data VAL edn "data piece for rendering"]
  (with-pre-wrap fileset
    (let [tmp (tmp-dir!)
          out (io/file tmp "dev.html")]
      (empty-dir! tmp)
      (spit out (html-dsl data fileset))
      (-> fileset
        (add-resource tmp)
        (commit!)))))

(deftask editor! []
  (comp
    (repl)
    (start-stack-editor!)
    (target :dir #{"src/"})))

(deftask dev! []
  (set-env!
    :asset-paths #{"assets/"})
  (comp
    (editor!)
    (html-file :data {:build? false})
    (reload :on-jsload 'recollect.main/on-jsload!
            :cljs-asset-path ".")
    (cljs :compiler-options {:language-in :ecmascript5})
    (target :no-clean true)))

(deftask generate-code []
  (comp
    (transform-stack :filename "stack-sepal.ir")
    (target :dir #{"src/"})))

(deftask build-advanced []
  (set-env!
    :asset-paths #{"assets/"})
  (comp
    (transform-stack :filename "stack-sepal.ir")
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5
                             :pseudo-names true
                             :static-fns true
                             :parallel-build true
                             :optimize-constants true
                             :source-map true})
    (html-file :data {:build? true})
    (target)))

(deftask rsync []
  (with-pre-wrap fileset
    (sh "rsync" "-r" "target/" "repo.tiye.me:repo/Cumulo/recollect" "--exclude" "main.out" "--delete")
    fileset))

(deftask build []
  (comp
    (transform-stack :filename "stack-sepal.ir")
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))

(deftask watch-test []
  (set-env!
    :source-paths #{"src" "test"})
  (comp
    (watch)
    (test :namespaces '#{recollect.test})))
