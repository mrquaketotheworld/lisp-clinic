(ns db.models.address
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [utils.format.address :as address-format]
            [db.config :refer [db-config]]))

(defn get-address-id [city street house]
  (:address/id (first (sql/find-by-keys db-config :address
                                        {:city city :street street :house house}))))

(defn add [connection city street house]
  (:address/id (sql/insert! connection :address
                            {:city city :street street
                             :house house} {:return-keys ["id"]})))

(defn get-cities []
  (address-format/take-cities-values (jdbc/execute! db-config ["SELECT DISTINCT city FROM address
                                                               ORDER BY city"]
                                                    {:builder-fn rs/as-unqualified-lower-maps})))
