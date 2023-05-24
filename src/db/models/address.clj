(ns db.models.address
  (:require [next.jdbc.sql :as sql]))

(defn match-address [db-con city street house]
  (first (sql/find-by-keys db-con :address {:city city :street street :house house})))
