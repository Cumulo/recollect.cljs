
(ns recollect.patch )

(defn patch-one [base change]
  (let [[coord op data] change]
    (case op
      :v/+ base
      :v/- base
      :m/+ base
      :m/- base
      :m/! base
      :st/+ base
      :st/- base
      (do (println "Unkown op:" op) base))))

(defn patch-view [base changes]
  (if (empty? changes) base (recur (patch-one base (first changes)) (rest changes))))
