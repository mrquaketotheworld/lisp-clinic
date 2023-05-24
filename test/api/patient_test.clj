(ns api.patient-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [next.jdbc :as jdbc]
            [ring.mock.request :as mock]
            [core]
            [config :as config-src-dir]
            [config-test :refer [db-test]]
            [db.init-tables :as init-tables]
            [db.models.patient :as patient]
            [db.models.address :as address]))

(defn mock-request [request-type url body]
  (core/wrapped-app (-> (mock/request request-type url)
                        (mock/json-body body))))

(defn mock-request-patient-add [body]
  (mock-request :post "/api/patient/add" body))

(defn mock-request-patient-edit [body]
  (mock-request :post "/api/patient/edit" body))

(defn patient-main-info [patient]
  (dissoc patient :birth :updated_at :created_at))

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
                                 :gender "      male    "
                                 :birth-day 25
                                 :birth-month 12
                                 :birth-year 1965
                                 :city "        New YorK"
                                 :street "Big apple       "
                                 :house 20
                                 :mid mid})
      (let [patient (patient/get-by-mid mid)
            patient-address (address/get-by-mid mid)]
        (is (= (patient-main-info patient) {:first_name "Homer"
                                            :last_name "Simpson"
                                            :gender_type "Male"
                                            :mid mid}))
        (is (= (:birth patient) "1965-12-25"))
        (is (= patient-address {:city "New York" :street "Big Apple" :house 20})))))

  (testing "Patient gets existing address"
    (let [city "New York" street "Yellow" house 22 same-address-second-mid "123426782327"]
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
                                 :mid same-address-second-mid})
      (let [patient-address (address/get-by-mid same-address-second-mid)
            addresses (address/match-address city street house)]
        (is (and (= patient-address {:city city
                                     :street street
                                     :house house}) (= 1 (count addresses)))))))

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
      (is (= "{\"success\":true}" (:body response)))))

  (testing "Validation, patient without field"
    (let [response (mock-request-patient-add {:first-name "Liza"
                                              :last-name "Simpson"
                                              :birth-day 30
                                              :birth-month 10
                                              :birth-year 1994
                                              :city "New york"
                                              :street "big apple"
                                              :house 20
                                              :mid "123426782328"})]
      (is (= "{\"error\":\"Validation error\"}" (:body response))))))

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

(deftest patient-edit
  (println 'RUN-PATIENT-EDIT)
  (testing "Edit was edited"
    (let [mid "243283439393" new-first-name "Slim" new-last-name "Shady" gender "Male"
          new-city "Detroit" new-street "Snow" new-house 25]
      (mock-request-patient-add {:first-name "Marshall"
                                 :last-name "Mather"
                                 :gender "Male"
                                 :birth-day 19
                                 :birth-month 7
                                 :birth-year 1970
                                 :city "Compton"
                                 :street "Smith"
                                 :house 24
                                 :mid mid})
      (mock-request-patient-edit {:first-name new-first-name
                                  :last-name new-last-name
                                  :gender gender
                                  :birth-day 19
                                  :birth-month 7
                                  :birth-year 1970
                                  :city new-city
                                  :street new-street
                                  :house new-house
                                  :mid mid})
      (let [patient (patient/get-by-mid mid)
            patient-address (address/get-by-mid mid)]
        (is (= (patient-main-info patient) {:first_name new-first-name
                                            :last_name new-last-name
                                            :gender_type gender
                                            :mid mid}))
        (is (= (:birth patient) "1970-07-19"))
        (is (= patient-address {:city new-city :street new-street :house new-house})))))
  (testing "Patient doesn't exist"
    (let [response (mock-request-patient-edit {:first-name "John"
                                               :last-name "Chan"
                                               :gender "Male"
                                               :birth-day 16
                                               :birth-month 8
                                               :birth-year 1981
                                               :city "Tokio"
                                               :street "Alex Yao"
                                               :house 2
                                               :mid "423838383838"})]
      (is (= "{\"error\":\"Patient doesn't exist\"}" (:body response))))))
