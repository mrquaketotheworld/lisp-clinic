(ns db.models.patient
  (:require [config :refer [db-config]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [db.models.address :as address])
  (:import [java.time LocalDate]))

(defn does-exist? [mid]
  (boolean (first (sql/find-by-keys db-config :patient {:mid mid}))))

(defn add [{:keys [first-name last-name gender birth-day birth-month birth-year city street house
                   mid]}]
  (jdbc/with-transaction [db-con db-config]
    (sql/insert! db-con :patient {:first_name first-name
                                  :last_name last-name
                                  :gender_type gender
                                  :birth (LocalDate/of birth-year birth-month birth-day)
                                  :mid mid})

    (let [address-id (address/match-address db-con city street house)]
      (if address-id
        (sql/insert! db-con :patient_address {:patient_mid mid
                                              :address_id (:address/id address-id)})
        (let [created-address-id (:address/id (address/add db-con city street house))]
          (sql/insert! db-con :patient_address {:patient_mid mid
                                                :address_id created-address-id}))))))

(defn delete [mid]
  (sql/delete! db-config :patient {:mid mid}))

(defn edit [{:keys [first-name last-name gender birth-day birth-month birth-year city street house
                    mid]}]
  (jdbc/with-transaction [db-con db-config]
    (sql/update! db-con :patient {:first_name first-name
                                  :last_name last-name
                                  :gender_type gender
                                  :birth (LocalDate/of birth-year birth-month birth-day)}
                 {:mid mid})
    (sql/delete! db-con :patient_address {:patient_mid mid})
    (let [address-id (address/match-address db-con city street house)]
      (if address-id
        (sql/update! db-con :patient_address {:address_id (:address/id address-id)}
                     {:patient_mid mid})
        (let [created-address-id (:address/id (address/add db-con city street house))]
          (sql/insert! db-con :patient_address {:patient_mid mid
                                                :address_id created-address-id}))))))

