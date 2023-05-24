(ns db.models.patient
  (:require [config :refer [db-config]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql])
  (:import [java.time LocalDate]))

(defn does-exist? [mid]
  (boolean (jdbc/execute-one! db-config ["SELECT mid FROM patient WHERE mid = ?" mid])))

(defn add! [{:keys [first-name last-name gender birth-day birth-month birth-year city street house
                   mid]}]
  (jdbc/with-transaction [db-con db-config]
    (sql/insert! db-con :patient {:first_name first-name
                                  :last_name last-name
                                  :gender_type gender
                                  :birth (LocalDate/of birth-year birth-month birth-day)
                                  :mid mid})

    (let [address-id (jdbc/execute-one! db-con ["SELECT id FROM address
                                                WHERE city = ? and street = ? and house = ?"
                                                city street house])]
      (if address-id
        (sql/insert! db-con :patient_address {:patient_mid mid
                                              :address_id (:address/id address-id)})
        (let [created-address-id (:address/id (sql/insert! db-con :address
                                                           {:city city :street street
                                                            :house house} {:return-keys ["id"]}))]
          (sql/insert! db-con :patient_address {:patient_mid mid
                                                :address_id created-address-id}))))))

(defn delete [mid]
  (jdbc/execute-one! db-config ["DELETE FROM patient WHERE mid = ?" mid]))

