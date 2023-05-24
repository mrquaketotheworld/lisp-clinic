(ns db.models.address
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [config :refer [db-config]]))

(defn match-address [db-con city street house]
  (:address/id (first (sql/find-by-keys db-con :address {:city city :street street :house house}))))

(defn add [db-con city street house]
  (:address/id (sql/insert! db-con :address
                            {:city city :street street
                             :house house} {:return-keys ["id"]})))

(defn get-by-mid [mid]
  (jdbc/execute-one! db-config ["SELECT city, street, house FROM address
                                WHERE id = (SELECT address_id FROM
                                patient_address WHERE patient_mid = ?)" mid]
                     {:builder-fn rs/as-unqualified-lower-maps}))
