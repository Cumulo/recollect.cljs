
(ns recollect.patch )

(defn patch-map-set [base coord data] (if (empty? coord) data (assoc-in base coord data)))

(defn patch-one [base change]
  (let [[coord op data] change]
    (case op
      :v/+! base
      :v/-! base
      :m/- base
      :m/! (patch-map-set base coord data)
      :st/++ base
      :st/-- base
      (do (println "Unkown op:" op) base))))

(defn patch-view [base changes]
  (if (empty? changes) base (recur (patch-one base (first changes)) (rest changes))))
