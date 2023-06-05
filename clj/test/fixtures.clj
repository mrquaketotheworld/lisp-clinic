(ns fixtures
  (:require [db.config :refer [db-config]]
            [next.jdbc :as jdbc]
            [db.init-tables :as init-tables]))

(defn db-fixture [tests-run]
  (jdbc/execute-one! db-config ["DROP TABLE IF EXISTS address, gender, patient"])
  (init-tables/init)
  (tests-run))

(defn clear-tables-fixture [test-run]
  (jdbc/execute! db-config ["DELETE FROM patient; DELETE FROM address"])
  (test-run))
