(ns db.models.patient-address
  (:require [next.jdbc.sql :as sql]))

(defn add [connection mid address-id]
  (sql/insert! connection :patient_address {:patient_mid mid
                                        :address_id address-id}))

(defn delete [connection mid]
  (sql/delete! connection :patient_address {:patient_mid mid}))
