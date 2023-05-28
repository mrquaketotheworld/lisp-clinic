(ns db.models.address
  (:require [next.jdbc.sql :as sql]
            [config :refer [db-config]]))

(defn get-address-id [city street house]
  (:address/id (first (sql/find-by-keys db-config :address
                                        {:city city :street street :house house}))))

(defn add [connection city street house]
  (:address/id (sql/insert! connection :address
                            {:city city :street street
                             :house house} {:return-keys ["id"]})))
