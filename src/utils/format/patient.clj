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
                :limit "10"
                :offset "0"} formatted-patient-form) ; TODO select max
        (update :age-bottom read-string)
        (update :age-top read-string)
        (update :limit read-string)
        (update :offset read-string))))

(defn format-date [patient]
  (assoc patient :birth (.toString (:birth patient))))

(defn format-patients [patients]
  (map #(format-date %) patients))

(defn format-patient-to-db-fields [patient]
  (let [format-patient-form (format-patient-form patient)]
    {:first_name (:first-name format-patient-form)
     :last_name (:last-name format-patient-form)
     :gender (:gender format-patient-form)
     :birth (.toString (LocalDate/of (:birth-year format-patient-form)
                                     (:birth-month format-patient-form)
                                     (:birth-day format-patient-form)))
     :city (:city format-patient-form)
     :street (:street format-patient-form)
     :house (:house format-patient-form)
     :mid (:mid format-patient-form)}))
