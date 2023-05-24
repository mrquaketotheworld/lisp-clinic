(ns db.models.patient-address
  (:require [next.jdbc.sql :as sql]))

(defn add [db-con mid address-id]
  (sql/insert! db-con :patient_address {:patient_mid mid
                                        :address_id address-id}))

(defn delete [db-con mid]
  (sql/delete! db-con :patient_address {:patient_mid mid}))
