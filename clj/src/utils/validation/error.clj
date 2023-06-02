(ns utils.validation.error
  (:require [clojure.spec.alpha :as spec]))

(def INPUT-MAXLENGTH 128)
(def MID-LENGTH 12)

(defn valid-input? [value]
  (<= (count (str value)) INPUT-MAXLENGTH))

(def max-length (str "Max length is: " INPUT-MAXLENGTH))

(def errors
  {:firstname max-length
   :lastname max-length
   :gender "Can be Male or Female"
   :birth "Format should be yyyy-mm-dd"
   :city max-length
   :street max-length
   :house "Should be a number"
   :mid (str "Length should be: " MID-LENGTH)
   :search max-length
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



