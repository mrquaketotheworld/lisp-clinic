(ns config-test
  (:require [utils.file.interact :as file]))

(def test-config (file/load-edn "test_config.edn"))
(def db-test {:dbtype (:dbtype test-config)
              :dbname (:dbname test-config)
              :host (:host test-config)
              :user (:user test-config)
              :password (:password test-config)})
