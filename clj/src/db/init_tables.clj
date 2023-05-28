(ns db.init-tables
  (:require [config :refer [db-config]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn create-table-gender []
  (jdbc/execute-one! db-config ["CREATE TABLE IF NOT EXISTS gender (
                                  gender VARCHAR(16) PRIMARY KEY,
                                  created_at timestamptz DEFAULT NOW() NOT NULL)"]))

(defn add-genders []
  (sql/insert-multi! db-config :gender [{:gender "Male"} {:gender "Female"}]))

(defn create-table-patient []
  (jdbc/execute-one! db-config ["CREATE TABLE IF NOT EXISTS patient (
                                  mid VARCHAR(12) PRIMARY KEY,
                                  first_name VARCHAR(128) NOT NULL,
                                  last_name VARCHAR(128) NOT NULL,
                                  gender VARCHAR(16) NOT NULL REFERENCES gender(gender),
                                  birth DATE NOT NULL,
                                  address_id INT NOT NULL REFERENCES address(id),
                                  created_at timestamptz DEFAULT NOW() NOT NULL,
                                  updated_at timestamptz DEFAULT NOW() NOT NULL)"]))

(defn add-patient-update-at-trigger []
  (jdbc/execute-one! db-config ["CREATE OR REPLACE FUNCTION trigger_set_timestamp()
                                  RETURNS TRIGGER AS $$
                                  BEGIN
                                    NEW.updated_at = NOW();
                                    RETURN NEW;
                                  END;
                                  $$ LANGUAGE plpgsql;
                                  CREATE TRIGGER set_timestamp
                                  BEFORE UPDATE ON patient
                                  FOR EACH ROW
                                    EXECUTE PROCEDURE trigger_set_timestamp()"]))

(defn create-table-address []
  (jdbc/execute-one! db-config ["CREATE TABLE IF NOT EXISTS address (
                                  id SERIAL PRIMARY KEY,
                                  city VARCHAR(128) NOT NULL,
                                  street VARCHAR(128) NOT NULL,
                                  house INT NOT NULL,
                                  created_at timestamptz DEFAULT NOW() NOT NULL,
                                  UNIQUE (city, street, house))"]))

(defn -main []
  (println 'INIT-TABLES)
  (create-table-gender)
  (add-genders)
  (create-table-address)
  (create-table-patient)
  (add-patient-update-at-trigger))
