(ns utils.format.patient
  (:require [clojure.string :as string]
            [utils.format.capitalize :as format-capitalize]))

(defn format-patient-form [patient-form]
  (reduce (fn [acc key-value]
            (let [key (first key-value) value (second key-value)]
              (if (string? value) (assoc acc key (-> value
                                                     string/trim
                                                     format-capitalize/capitalize-words))
                  (assoc acc key value)))) {} patient-form))
