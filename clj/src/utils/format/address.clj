(ns utils.format.address)

(defn take-cities-values [cities]
  (map #(:city %) cities))
