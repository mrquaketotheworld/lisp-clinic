(ns utils.validation.error
  (:require [clojure.spec.alpha :as spec]))

(def NAME-MAXLENGTH 128)
(def CITY-MAXLENGTH 128)
(def STREET-MAXLENGTH 128)
(def MID-LENGTH 12)

(defn max-length [max]
  (str "Max length is: " max))

(def errors
  {:firstname (max-length NAME-MAXLENGTH)
   :lastname (max-length NAME-MAXLENGTH)
   :gender "Can be Male or Female"
   :birth "Format should be yyyy-mm-dd"
   :city (max-length CITY-MAXLENGTH)
   :street (max-length STREET-MAXLENGTH)
   :house "Should be a number"
   :mid (str "Length should be: " MID-LENGTH)})

(defn validate [spec-item item]
  (let [explained-data (::spec/problems (spec/explain-data spec-item item))
        error (reduce (fn [acc problem]
                        (let [key-error (first (:path problem))]
                          (if key-error
                            (assoc acc key-error (key-error errors))
                            (assoc acc :some-of-keys-are-not-valid (keys (:val problem)))))) {}
                      explained-data)]
    (when (seq error) error)))



