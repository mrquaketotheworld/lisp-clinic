(ns utils.format.patient
  (:require [clojure.string :as string]
            [utils.format.capitalize :as format-capitalize])
  (:import [java.time LocalDate]))

(defn format-patient-form [patient-form]
  (reduce (fn [acc key-value]
            (let [key (first key-value) value (second key-value)]
              (if (string? value) (assoc acc key (-> value
                                                     string/trim
                                                     format-capitalize/capitalize-words))
                  (assoc acc key value)))) {} patient-form))

(defn format-patient-search-form [patient-form]
  (let [formatted-patient-form (format-patient-form patient-form)]
    (-> (merge {:first-name ""
                :last-name ""
                :gender ""
                :mid ""
                :city ""
                :age-bottom "0"
                :age-top "100"
                :offset "0"} formatted-patient-form) ; TODO select max
        (update :age-bottom read-string)
        (update :age-top read-string)
        (update :offset read-string))))

(defn format-date [patient]
  (assoc patient :birth (.toString (:birth patient))))

(defn format-patients [patients]
  (map #(format-date %) patients))

(defn format-patient-to-db-fields [patient]
  (format-patient-form {:first_name (:first-name patient)
                        :last_name (:last-name patient)
                        :gender (:gender patient)
                        :birth (.toString (LocalDate/of (:birth-year patient)
                                                        (:birth-month patient)
                                                        (:birth-day patient)))
                        :city (:city patient)
                        :street (:street patient)
                        :house (:house patient)
                        :mid (:mid patient)}))
