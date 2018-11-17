(ns recollect.twig)

(defmacro deftwig [twig-name params & body]
  `(defn ~twig-name [~@params]
    (recollect.types/Twig. ~(keyword twig-name)
      (list ~@params)
      (do ~@body)
      (fn [~@params] ~@body))))
