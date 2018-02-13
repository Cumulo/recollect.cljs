
(defn read-password [guide]
  (String/valueOf (.readPassword (System/console) guide nil)))

(set-env!
  :resource-paths #{"src" "polyfill"}
  :dependencies '[]
  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                     :username "jiyinyiyong"
                                     :password (read-password "Clojars password: ")}]))

(def +version+ "0.4.3")

(deftask deploy []
  (comp
    (pom :project     'cumulo/recollect
         :version     +version+
         :description "Cached rendering and diff/patch library designed for Cumulo project."
         :url         "https://github.com/Cumulo/recollect"
         :scm         {:url "https://github.com/Cumulo/recollect"}
         :license     {"MIT" "http://opensource.org/licenses/mit-license.php"})
    (jar)
    (install)
    (push :repo "clojars" :gpg-sign false)))
