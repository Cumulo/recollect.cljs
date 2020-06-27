(ns recollect.twig)

(defmacro deftwig [twig-name params & body]
  (assert (symbol? twig-name) "1st argument should be a symbol")
  (assert (coll? params) "2nd argument should be a collection")
  (assert (some? (last body)) "deftwig should return something")
  (let [twig-helper (gensym "twig-helper-")]
  `(do
     (defn ~twig-helper [~@params] ~@body)
     (defn ~twig-name [~@params] (recollect.twig/call-twig-func ~twig-helper [~@params])))))
