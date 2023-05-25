(ns api.patient-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [next.jdbc :as jdbc]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [core]
            [config :as config-src-dir]
            [config-test :refer [db-test]]
            [db.init-tables :as init-tables]
            [db.models.address :as address]
            [utils.format.message :refer [PATIENT-DOESNT-EXIST VALIDATION-ERROR PATIENT-EXISTS]]))

(defn mock-request [request-type url]
  (:body (core/wrapped-app (mock/request request-type url))))

(defn mock-request-get [url]
  (mock-request :get url))

(defn mock-request-delete [mid]
  (mock-request :delete (str "/api/patient/delete/" mid)))

(defn mock-request-post [url body]
  (:body (core/wrapped-app (-> (mock/request :post url)
                               (mock/json-body body)))))

(defn mock-request-patient-add [body]
  (mock-request-post "/api/patient/add" body))

(defn json-parse-body [body]
  (json/parse-string body true))

(defn mock-request-patient-get-by-mid [mid]
  (json-parse-body (mock-request-get (str "/api/patient/get/" mid))))

(defn mock-request-patient-search [query-string]
  (json-parse-body (mock-request-get (str "/api/patient/search?" query-string))))

(defn json-parse-error [body]
  (:error (json-parse-body body)))

(defn json-parse-success [body]
  (:success (json-parse-body body)))

(defn mock-request-patient-edit [body]
  (mock-request-post "/api/patient/edit" body))

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
      (let [patient (mock-request-patient-get-by-mid mid)]
        (is (= patient {:first_name "Homer"
                        :last_name "Simpson"
                        :gender "Male"
                        :city "New York"
                        :street "Big Apple"
                        :house 20
                        :birth "1965-12-25"
                        :mid mid})))))

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
      (let [body (mock-request-patient-add patient)]
        (is (= PATIENT-EXISTS (json-parse-error body))))))

  (testing "Patient was saved"
    (let [body (mock-request-patient-add {:first-name "Liza"
                                          :last-name "Simpson"
                                          :gender "Female"
                                          :birth-day 30
                                          :birth-month 10
                                          :birth-year 1994
                                          :city "New york"
                                          :street "big apple"
                                          :house 20
                                          :mid "123426782328"})]
      (is (json-parse-success body))))

  (testing "Validation, patient without field"
    (let [body (mock-request-patient-add {:first-name "Liza"
                                          :last-name "Simpson"
                                          :birth-day 30
                                          :birth-month 10
                                          :birth-year 1994
                                          :city "New york"
                                          :street "big apple"
                                          :house 20
                                          :mid "123426782328"})]
      (is (= VALIDATION-ERROR (json-parse-error body))))))

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
      (let [body (mock-request-delete mid)
            patient (mock-request-patient-get-by-mid mid)]
        (is (and (json-parse-success body) (= PATIENT-DOESNT-EXIST (:error patient))))))))

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
      (let [patient (mock-request-patient-get-by-mid mid)]
        (is (= patient {:first_name new-first-name
                        :last_name new-last-name
                        :gender gender
                        :birth "1970-07-19"
                        :city new-city
                        :street new-street
                        :house new-house
                        :mid mid})))))

  (testing "Patient doesn't exist"
    (let [body (mock-request-patient-edit {:first-name "John"
                                           :last-name "Chan"
                                           :gender "Male"
                                           :birth-day 16
                                           :birth-month 8
                                           :birth-year 1981
                                           :city "Tokio"
                                           :street "Alex Yao"
                                           :house 2
                                           :mid "423838383838"})]
      (is (= PATIENT-DOESNT-EXIST (json-parse-error body))))))

(deftest patient-get
  (println 'RUN-PATIENT-GET)

  (testing "Get patient by mid"
    (let [mid "34239202023f" first-name "Michael" last-name "Moe" gender "Male" city "Boston"
          street "Flinstone" house 273]
      (mock-request-patient-add {:first-name first-name
                                 :last-name last-name
                                 :gender gender
                                 :birth-day 21
                                 :birth-month 8
                                 :birth-year 1973
                                 :city city
                                 :street street
                                 :house house
                                 :mid mid})
      (let [patient (mock-request-patient-get-by-mid mid)]
        (is (= patient {:first_name first-name
                        :last_name last-name
                        :gender gender
                        :birth "1973-08-21"
                        :city city
                        :street street
                        :house house
                        :mid mid})))))

  (testing "Get patient by unknown mid"
    (let [patient (mock-request-patient-get-by-mid "unknownmid32")]
      (is (= PATIENT-DOESNT-EXIST (:error patient))))))

#_(deftest patient-search
    (testing "Context of the test assertions"
      (is (= assertion-values))))
