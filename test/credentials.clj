(ns credentials)

(def test-config (load-config "test_config.edn"))
(def test-dbtype (:dbtype test-config))
(def test-dbname (:dbname test-config))
(def test-host (:host test-config))
(def test-user (:user test-config))
(def test-password (:password test-config))
(def db-test {:dbtype test-dbtype
              :dbname test-dbname
              :host test-host
              :user test-user
              :password test-password})
