(ns db.models.patient
  (:require [db.config :refer [db-config]]
            [clojure.string :as string]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [next.jdbc.date-time :as date-time]
            [db.models.address :as address]
            [utils.format.patient :as patient-format]
            [utils.format.time :as time]))

(date-time/read-as-local)

(defn get-by-mid [mid]
  (when-let [patient (jdbc/execute-one! db-config ["SELECT firstname, lastname, gender,
                                                     birth, city, street, house, mid
                                                     FROM patient
                                                     JOIN address ON patient.address_id =
                                                     address.id WHERE patient.mid = ?" mid]
                                        {:builder-fn rs/as-unqualified-lower-maps})]
    (patient-format/format-date patient)))

(defn get-address-id [connection city street house]
  (if-let [address-id (address/get-address-id city street house)]
    address-id
    (address/add connection city street house)))

(defn add [{:keys [firstname lastname gender birth city street house mid]}]
  (jdbc/with-transaction [connection db-config]
    (sql/insert! connection :patient {:firstname firstname
                                      :lastname lastname
                                      :gender gender
                                      :birth (time/parse-date birth)
                                      :address_id (get-address-id connection city street house)
                                      :mid mid})))

(defn delete [mid]
  (sql/delete! db-config :patient {:mid mid}))

(defn edit [{:keys [firstname lastname gender birth city street house mid]}]
  (jdbc/with-transaction [connection db-config]
    (sql/update! connection :patient {:firstname firstname
                                      :lastname lastname
                                      :gender gender
                                      :birth (time/parse-date birth)
                                      :address_id (get-address-id connection city street house)}
                 {:mid mid})))

(defn search [{:keys [search gender city age-bottom age-top]}]
  (let [empty-city? (empty? city)
        empty-gender? (empty? gender)
        splitted-search (string/join "|" (string/split search #" "))
        params [age-bottom (inc age-top) splitted-search splitted-search splitted-search]
        params-city-optional (if empty-city? params (cons city params))
        params-city-gender-optional (if empty-gender? params-city-optional
                                        (cons gender params-city-optional))
        patients (jdbc/execute! db-config
                          (concat [(str
                         "SELECT firstname, lastname, gender, birth, city, street, house, mid
                             FROM patient
                           JOIN address ON patient.address_id = address.id
                             WHERE " (when-not empty-gender? "patient.gender = ? AND ")
                                     (when-not empty-city? "address.city = ? AND ")
                              "AGE(patient.birth) BETWEEN CAST(? || ' years' AS interval)
                               AND CAST(? || ' years' AS interval) AND
                               (patient.firstname ~* ? OR patient.lastname ~* ?
                          OR patient.mid ~* ?) ORDER BY patient.created_at DESC")]
                                  params-city-gender-optional)
                                {:builder-fn rs/as-unqualified-lower-maps})]
    (patient-format/format-patients patients)))
