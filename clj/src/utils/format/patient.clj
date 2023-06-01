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

(defn format-patient-to-db-fields [patient]
  (let [format-patient-form (format-patient-form patient)]
    (-> format-patient-form
        (merge {:first_name (:first-name format-patient-form)
                :last_name (:last-name format-patient-form)})
        (dissoc :first-name :last-name))))
