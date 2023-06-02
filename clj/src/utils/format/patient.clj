(ns utils.format.patient
  (:require [clojure.string :as string]
            [utils.format.capitalize :as format-capitalize]))

(defn format-patient-form [patient-form]
  (reduce (fn [acc key-value]
            (let [key (first key-value) value (second key-value)]
              (if (= key :mid)
                (assoc acc key value)
                (assoc acc key (-> value
                                   string/trim
                                   format-capitalize/capitalize-words))))) {} patient-form))

(defn format-patient-search-form [patient-form]
  (let [formatted-patient-form (format-patient-form patient-form)]
    (-> (merge {:gender ""
                :search ""
                :city ""
                :age-bottom "0"
                :age-top "100"
                :limit "10"
                :offset "0"} formatted-patient-form)
        (update :age-bottom read-string)
        (update :age-top read-string)
        (update :limit read-string)
        (update :offset read-string))))

(defn format-date [patient]
  (assoc patient :birth (.toString (:birth patient))))

(defn format-patients [patients]
  (map #(format-date %) patients))
