(ns api.patient-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [ring.mock.request :as mock]
            [core]
            [config :as config-src-dir]
            [config-test :refer [db-test]]
            [db.init-tables :as init-tables]
            [db.models.patient :as patient]))

(defn mock-request [request-type url body]
  (core/wrapped-app (-> (mock/request request-type url)
                        (mock/json-body body))))

(defn mock-request-patient-add [body]
  (mock-request :post "/api/patient/add" body))

(defn db-fixture [test-run]
  (config-src-dir/set-config! db-test) ; make global TEST configuration
  (jdbc/execute-one! db-test ["DROP TABLE IF EXISTS address, gender, patient, patient_address"])
  (init-tables/-main)
  (test-run))
(use-fixtures :once db-fixture)

(deftest patient-add
  (println 'RUN-PATIENT-ADD)
  (testing "Patient was saved formatted"
    (let [mid "123426782326"]
      (mock-request-patient-add {:first-name "Homer"
                                 :last-name "Simpson"
                                 :gender "male"
                                 :birth-day 25
                                 :birth-month 12
                                 :birth-year 1965
                                 :city "        New YorK"
                                 :street "Big apple"
                                 :house 20
                                 :mid mid})
      (let [patient (first (sql/find-by-keys db-test :patient {:mid mid}
                                             {:builder-fn rs/as-unqualified-lower-maps}))]
        (is (= (dissoc patient :birth :updated_at :created_at) {:first_name "Homer"
                                                                :last_name "Simpson"
                                                                :gender_type "Male"
                                                                :mid mid}))
        (is (= (.toString (:birth patient)) "1965-12-25")))
      (let [patient-address (jdbc/execute-one! db-test ["SELECT city, street, house FROM address
                                                        WHERE id = (SELECT address_id FROM
                                                        patient_address WHERE patient_mid = ?)"
                                                        mid]
                                               {:builder-fn rs/as-unqualified-lower-maps})]
        (is (= patient-address {:city "New York" :street "Big Apple" :house 20})))))

  (testing "Patient gets existing address"
    (let [city "New York" street "Yellow" house 22]
      (mock-request-patient-add {:first-name "Santa"
                                 :last-name "Helper"
                                 :gender "male"
                                 :birth-day 2
                                 :birth-month 1
                                 :birth-year 1999
                                 :city city
                                 :street street
                                 :house house
                                 :mid "023426782327"})
      (mock-request-patient-add {:first-name "Bart"
                                 :last-name "Simpson"
                                 :gender "male"
                                 :birth-day 20
                                 :birth-month 11
                                 :birth-year 1989
                                 :city city
                                 :street street
                                 :house house
                                 :mid "123426782327"})
      (let [patient-address (jdbc/execute-one! db-test ["SELECT city, street, house FROM address
                                              WHERE id = (SELECT address_id FROM patient_address
                                                           WHERE patient_mid = ?)" "123426782327"]
                                               {:builder-fn rs/as-unqualified-lower-maps})
            addresses (sql/find-by-keys db-test :address {:city city
                                                          :street street
                                                          :house 22})]
        (is (and (= patient-address {:city city
                                     :street street
                                     :house 22}) (= 1 (count addresses)))))))

  (testing "Patient already exists"
    (let [patient {:first-name "Anonymous"
                   :last-name "Simpson"
                   :gender "male"
                   :birth-day 20
                   :birth-month 11
                   :birth-year 1989
                   :city "New york"
                   :street "big apple"
                   :house 20
                   :mid "153426782327"}]
      (mock-request-patient-add patient)
      (let [response (mock-request-patient-add patient)]
        (is (= "{\"error\":\"Patient already exists\"}" (:body response))))))

  (testing "Patient was saved"
    (let [response (mock-request-patient-add {:first-name "Liza"
                                              :last-name "Simpson"
                                              :gender "Female"
                                              :birth-day 30
                                              :birth-month 10
                                              :birth-year 1994
                                              :city "New york"
                                              :street "big apple"
                                              :house 20
                                              :mid "123426782328"})]
      (is (= "{\"success\":true}" (:body response))))))

(deftest patient-delete
  (println 'RUN-PATIENT-DELETE)
  (testing "Patient was deleted"
    (let [mid "123426782329"]
      (mock-request-patient-add {:first-name "Marge"
                                 :last-name "Simpson"
                                 :gender "Female"
                                 :birth-day 19
                                 :birth-month 7
                                 :birth-year 1970
                                 :city "New york"
                                 :street "big apple"
                                 :house 20
                                 :mid mid})
      (let [response (mock-request :delete "/api/patient/delete" {:mid mid})
            patient (patient/get-by-mid mid)]
        (is (and (= "{\"success\":true}" (:body response)) (nil? patient)))))))

#_(deftest patient-edit
    (println 'RUN-PATIENT-EDIT)
    (testing "Context of the test assertions"
      (is (= assertion-values))))
