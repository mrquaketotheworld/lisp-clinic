(ns db.models.address
  (:require [next.jdbc :as jdbc]))

(defn match-address [db-con city street house]
  (jdbc/execute-one! db-con ["SELECT id FROM address WHERE city = ? and street = ? and house = ?"
                             city street house]))
