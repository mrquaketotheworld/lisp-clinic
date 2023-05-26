(ns db.models.patient
  (:require [config :refer [db-config]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [next.jdbc.date-time :as date-time]
            [db.models.address :as address]
            [db.models.patient-address :as patient-address]
            [utils.format.patient :as patient-format])
  (:import [java.time LocalDate]))

(date-time/read-as-local)

(defn get-by-mid [mid]
  (when-let [patient (first (sql/find-by-keys db-config :patient {:mid mid}
                                              {:builder-fn rs/as-unqualified-lower-maps}))]
    (let [patient-address (address/get-by-mid mid)]
      (-> patient
          patient-format/format-date
          (dissoc :updated_at :created_at)
          (merge patient-address)))))

(defn assign-address [connection mid city street house]
  (let [address-id (address/get-address-id city street house)]
    (if address-id
      (patient-address/add connection mid address-id)
      (let [created-address-id (address/add connection city street house)]
        (patient-address/add connection mid created-address-id)))))

(defn add [{:keys [first-name last-name gender birth-day birth-month birth-year city street house
                   mid]}]
  (jdbc/with-transaction [connection db-config]
    (sql/insert! connection :patient {:first_name first-name
                                      :last_name last-name
                                      :gender gender
                                      :birth (LocalDate/of birth-year birth-month birth-day)
                                      :mid mid})
    (assign-address connection mid city street house)))

(defn delete [mid]
  (sql/delete! db-config :patient {:mid mid}))

(defn edit [{:keys [first-name last-name gender birth-day birth-month birth-year city street house
                    mid]}]
  (jdbc/with-transaction [connection db-config]
    (sql/update! connection :patient {:first_name first-name
                                      :last_name last-name
                                      :gender gender
                                      :birth (LocalDate/of birth-year birth-month birth-day)}
                 {:mid mid})
    (patient-address/delete connection mid)
    (assign-address connection mid city street house)))

(defn search [{:keys [first-name last-name mid gender city age-bottom age-top limit offset]}]
  (let [empty-city? (empty? city)
        empty-gender? (empty? gender)
        params [age-bottom (inc age-top) first-name last-name mid limit offset]
        params-city-optional (if empty-city? params (cons city params))
        params-city-gender-optional (if empty-gender? params-city-optional
                                        (cons gender params-city-optional))
        patients (jdbc/execute! db-config
                  (concat [(str "SELECT first_name, last_name, gender, birth, city, street, house,
                                    mid
                                   FROM patient
                                   JOIN patient_address ON patient.mid =
                                     patient_address.patient_mid
                                   JOIN address ON patient_address.address_id = address.id
                                   WHERE " (when-not empty-gender? "patient.gender = ? AND ")
                                            (when-not empty-city? "address.city = ? AND ")
                                     "AGE(patient.birth) BETWEEN CAST(? || ' years' AS interval)
                                     AND CAST(? || ' years' AS interval) AND
                                     (patient.first_name ~* ? AND patient.last_name ~* ?
                                     AND patient.mid ~* ?) LIMIT ? OFFSET ?")]
                                        params-city-gender-optional)
                                {:builder-fn rs/as-unqualified-lower-maps})]
    (patient-format/format-patients patients)))
