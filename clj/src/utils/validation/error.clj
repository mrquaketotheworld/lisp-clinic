(ns utils.validation.error
  (:require [clojure.spec.alpha :as spec]))

(def INPUT-MAXLENGTH 128)
(def MID-LENGTH 12)

(defn valid-input? [value]
  (<= (count (str value)) INPUT-MAXLENGTH))

(defn max-length [max]
  (str "Max length is: " max))

(def errors
  {:firstname (max-length INPUT-MAXLENGTH)
   :lastname (max-length INPUT-MAXLENGTH)
   :gender "Can be Male or Female"
   :birth "Format should be yyyy-mm-dd"
   :city (max-length INPUT-MAXLENGTH)
   :street (max-length INPUT-MAXLENGTH)
   :house "Should be a number"
   :mid (str "Length should be: " MID-LENGTH)
   :search (max-length INPUT-MAXLENGTH)
   :patient-doesnt-exist "Patient doesn't exist"
   :patient-exists "Patient already exists"})

(defn validate [spec-item item]
  (let [explained-data (::spec/problems (spec/explain-data spec-item item))
        error (reduce (fn [acc problem]
                        (let [key-error (first (:path problem))]
                          (if key-error
                            (assoc acc key-error (key-error errors))
                            (assoc acc :keys-missing-or-not-valid (keys (:val problem)))))) {}
                      explained-data)]
    (when (seq error) error)))



