
(set-env!
  :resource-paths #{"src"}
  :dependencies '[])

(def +version+ "0.1.7")

(deftask build []
  (comp
    (pom :project     'cumulo/recollect
         :version     +version+
         :description "Cached rendering and diff/patch library designed for Cumulo project."
         :url         "https://github.com/Cumulo/recollect"
         :scm         {:url "https://github.com/Cumulo/recollect"}
         :license     {"MIT" "http://opensource.org/licenses/mit-license.php"})
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
