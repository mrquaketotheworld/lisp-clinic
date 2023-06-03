(ns fixtures
  (:require [config :as config-src-dir]
            [next.jdbc :as jdbc]
            [db.init-tables :as init-tables]))

(def db-test {:dbtype "postgresql"
              :dbname "postgres"
              :host "localhost"
              :user "postgres"
              :password "postgres"})

(defn db-fixture [test-run]
  (config-src-dir/set-config! db-test) ; make global TEST configuration
  (jdbc/execute-one! db-test ["DROP TABLE IF EXISTS address, gender, patient"])
  (init-tables/-main)
  (test-run))

(defn clear-tables-fixture [test-run]
  (jdbc/execute! db-test ["DELETE FROM patient;
                           DELETE FROM address"])
  (test-run))
