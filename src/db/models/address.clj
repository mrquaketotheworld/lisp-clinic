(ns db.models.address
  (:require [next.jdbc.sql :as sql]))

(defn match-address [db-con city street house]
  (first (sql/find-by-keys db-con :address {:city city :street street :house house})))

(defn add [db-con city street house]
  (sql/insert! db-con :address
               {:city city :street street
                :house house} {:return-keys ["id"]}))
