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
    (let [patient-address (address/get-by-mid mid)]
      (-> patient
          (assoc :birth (.toString (:birth patient)))
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

