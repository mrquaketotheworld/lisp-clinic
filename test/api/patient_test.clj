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
  (testing "Patient was saved formatted"
    (core/wrapped-app (-> (mock/request :post "/api/patient/add")
                          (mock/json-body {:first-name "Homer"
                                           :last-name "Simpson"
                                           :gender "male"
                                           :birth-day 25
                                           :birth-month 12
                                           :birth-year 1965
                                           :city "        New YorK"
                                           :street "Big apple"
                                           :house 20
                                           :mid "123426782326"})))
    (let [patient (jdbc/execute-one! db-test ["SELECT first_name, last_name, gender_type, mid,
                                              birth
                                              FROM patient"]
                                     {:builder-fn rs/as-unqualified-lower-maps})]
      (is (= (dissoc patient :birth) {:first_name "Homer"
                                      :last_name "Simpson"
                                      :gender_type "Male"
                                      :mid "123426782326"}))
      (is (= (.toString (:birth patient)) "1965-12-25"))))
  (testing "Patient's address was saved"
    (let [patient-address (jdbc/execute-one! db-test ["SELECT city, street, house FROM address
                                              WHERE id = (SELECT address_id FROM patient_address
                                                           WHERE patient_mid = ?)" "123426782326"]
                                             {:builder-fn rs/as-unqualified-lower-maps})]

      (is (= patient-address {:city "New York"
                              :street "Big Apple"
                              :house 20}))))
  (testing "Next patient has address like previous one and address unique"
    (core/wrapped-app (-> (mock/request :post "/api/patient/add")
                          (mock/json-body {:first-name "Bart"
                                           :last-name "Simpson"
                                           :gender "male"
                                           :birth-day 20
                                           :birth-month 11
                                           :birth-year 1989
                                           :city "New york"
                                           :street "big apple"
                                           :house 20
                                           :mid "123426782327"})))
    (let [patient-address (jdbc/execute-one! db-test ["SELECT city, street, house FROM address
                                              WHERE id = (SELECT address_id FROM patient_address
                                                           WHERE patient_mid = ?)" "123426782327"]
                                             {:builder-fn rs/as-unqualified-lower-maps})
          addresses (jdbc/execute! db-test ["SELECT * FROM address"])]

      (is (and (= patient-address {:city "New York"
                                   :street "Big Apple"
                                   :house 20}) (= 1 (count addresses))))))
  (testing "User already exists"
    (let [response (core/wrapped-app (-> (mock/request :post "/api/patient/add")
                                         (mock/json-body {:first-name "Bart"
                                                          :last-name "Simpson"
                                                          :gender "male"
                                                          :birth-day 20
                                                          :birth-month 11
                                                          :birth-year 1989
                                                          :city "New york"
                                                          :street "big apple"
                                                          :house 20
                                                          :mid "123426782327"})))]
      (is (= "{\"error\":\"User already exists\"}" (:body response)))))
  (testing "User successfully saved"
    (let [response (core/wrapped-app (-> (mock/request :post "/api/patient/add")
                                         (mock/json-body {:first-name "Liza"
                                                          :last-name "Simpson"
                                                          :gender "male"
                                                          :birth-day 30
                                                          :birth-month 10
                                                          :birth-year 1994
                                                          :city "New york"
                                                          :street "big apple"
                                                          :house 20
                                                          :mid "123426782328"})))]
      (is (= "{\"success\":true}" (:body response))))))

