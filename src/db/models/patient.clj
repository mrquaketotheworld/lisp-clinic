(ns db.models.patient
  (:require [config :refer [db-config]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [db.models.address :as address]
            [db.models.patient-address :as patient-address])
  (:import [java.time LocalDate]))

(defn get-by-mid [mid]
  (when-let [patient (first (sql/find-by-keys db-config :patient {:mid mid}
                                              {:builder-fn rs/as-unqualified-lower-maps}))]
    (assoc patient :birth (.toString (:birth patient)))))

(defn assign-address [db-con mid city street house]
  (let [address-id (address/get-address-id city street house)]
    (if address-id
      (patient-address/add db-con mid address-id)
      (let [created-address-id (address/add db-con city street house)]
        (patient-address/add db-con mid created-address-id)))))

(defn add [{:keys [first-name last-name gender birth-day birth-month birth-year city street house
                   mid]}]
  (jdbc/with-transaction [db-con db-config]
    (sql/insert! db-con :patient {:first_name first-name
                                  :last_name last-name
                                  :gender_type gender
                                  :birth (LocalDate/of birth-year birth-month birth-day)
                                  :mid mid})
    (assign-address db-con mid city street house)))

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
    (patient-address/delete db-con mid)
    (assign-address db-con mid city street house)))

