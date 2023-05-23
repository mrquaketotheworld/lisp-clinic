(ns api.patient-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [ring.mock.request :as mock]
            [core]
            [config :as config-src-dir]
            [config-test :refer [db-test]]
            [db.init-tables :as init-tables]))

(defn db-fixture [test-run]
  (config-src-dir/set-config! db-test) ; make global TEST configuration
  (jdbc/execute-one! db-test ["DROP TABLE IF EXISTS address, gender, patient,
                                              patient_address"])
  (init-tables/-main)
  (test-run))
(use-fixtures :once db-fixture)

(deftest patient-add
  (core/wrapped-app (-> (mock/request :post "/api/patient/add")
                        (mock/json-body {:first-name "Homer"
                                         :last-name "Simpson"
                                         :gender "male"
                                         :birth-day 25
                                         :birth-month 12
                                         :birth-year 1991
                                         :city "        New YorK"
                                         :street "Big apple"
                                         :house 20
                                         :mid "123426782326"})))
  (testing "Patient was saved formatted"
    (let [patient (jdbc/execute-one! db-test ["SELECT first_name, last_name, gender_type, mid,
                                              birth
                                              FROM patient"]
                                     {:builder-fn rs/as-unqualified-lower-maps})]
      (is (= (dissoc patient :birth) {:first_name "Homer"
                                      :last_name "Simpson"
                                      :gender_type "Male"
                                      :mid "123426782326"}))
      (is (= (.toString (:birth patient)) "1991-12-25")))))

