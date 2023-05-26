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
            [utils.format.message :refer [PATIENT-DOESNT-EXIST VALIDATION-ERROR PATIENT-EXISTS]]
            [utils.format.patient :as patient-format]))

(defn json-parse-body [body]
  (json/parse-string body true))

(defn mock-request [request-type url]
  (-> (mock/request request-type url)
      core/wrapped-app
      :body
      json-parse-body))

(defn mock-request-get [url]
  (mock-request :get url))

(defn mock-request-delete [mid]
  (mock-request :delete (str "/api/patient/delete/" mid)))

(defn mock-request-post [url body]
  (-> (mock/request :post url)
      (mock/json-body body)
      core/wrapped-app
      :body
      json-parse-body))

(defn mock-request-patient-add [body]
  (mock-request-post "/api/patient/add" body))

(defn mock-request-patient-get-by-mid [mid]
  (-> (str "/api/patient/get/" mid)
      mock-request-get))

(defn mock-request-patient-search [query-string]
  (mock-request-get (str "/api/patient/search?" query-string)))

(defn mock-request-patient-edit [body]
  (mock-request-post "/api/patient/edit" body))

(defn patients-count-equals? [patients value]
  (= (count patients) value))

(defn found-patient-equals? [patients-found patient]
  (is (and (= (first patients-found) (patient-format/format-patient-to-db-fields patient))
           (patients-count-equals? patients-found 1))))

(defn equals-error? [message error]
  (is (= message (:error error))))

(defn is-validation-error? [error]
  (equals-error? VALIDATION-ERROR error))

(defn patient-doesnt-exist? [patient]
  (equals-error? PATIENT-DOESNT-EXIST patient))

(defn db-fixture [test-run]
  (config-src-dir/set-config! db-test) ; make global TEST configuration
  (jdbc/execute-one! db-test ["DROP TABLE IF EXISTS address, gender, patient, patient_address"])
  (init-tables/-main)
  (test-run))
(use-fixtures :once db-fixture)

(defn clear-tables-fixture [test-run]
  (jdbc/execute! db-test ["DELETE FROM patient;
                           DELETE FROM patient_address;
                           DELETE FROM address"])
  (test-run))
(use-fixtures :each clear-tables-fixture)

(deftest patient-add
  (println 'RUN-PATIENT-ADD)

  (testing "Patient was saved formatted"
    (let [homer-simpson {:first-name "Homer"
                         :last-name "Simpson"
                         :gender "      male    "
                         :birth-day 25
                         :birth-month 12
                         :birth-year 1965
                         :city "        New YorK"
                         :street "Big apple       "
                         :house 20
                         :mid  "123426782326"}]
      (mock-request-patient-add homer-simpson)
      (let [patient (mock-request-patient-get-by-mid (:mid homer-simpson))]
        (is (= patient (patient-format/format-patient-to-db-fields homer-simpson))))))

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
        (equals-error? PATIENT-EXISTS body))))

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
      (is (:success body))))

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
      (is-validation-error? body))))

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
        (is (:success body))
        (patient-doesnt-exist? patient)))))

(deftest patient-edit
  (println 'RUN-PATIENT-EDIT)

  (testing "Patient was edited"
    (let [slim-shady-edit-patient-form {:mid "243283439393"
                                        :first-name "Slim"
                                        :last-name "Shady"
                                        :gender "Male"
                                        :city "Detroit"
                                        :street "Snow"
                                        :birth-day 19
                                        :birth-month 7
                                        :birth-year 1970
                                        :house 25}]
      (mock-request-patient-add {:first-name "Marshall"
                                 :last-name "Mather"
                                 :gender "Male"
                                 :birth-day 19
                                 :birth-month 7
                                 :birth-year 1970
                                 :city "Compton"
                                 :street "Smith"
                                 :house 24
                                 :mid (:mid slim-shady-edit-patient-form)})
      (mock-request-patient-edit slim-shady-edit-patient-form)
      (let [patient (mock-request-patient-get-by-mid (:mid slim-shady-edit-patient-form))]
        (is (= patient (patient-format/format-patient-to-db-fields
                        slim-shady-edit-patient-form))))))

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
      (patient-doesnt-exist? body))))

(deftest patient-get
  (println 'RUN-PATIENT-GET)

  (testing "Get patient by mid"
    (let [michael-moe {:mid "34239202023f"
                       :first-name "Michael"
                       :last-name "Moe"
                       :gender "Male"
                       :city "Boston"
                       :street "Flinstone"
                       :birth-day 21
                       :birth-month 8
                       :birth-year 1973
                       :house 273}]
      (mock-request-patient-add michael-moe)
      (let [patient (mock-request-patient-get-by-mid (:mid michael-moe))]
        (is (= patient (patient-format/format-patient-to-db-fields michael-moe))))))

  (testing "Get patient by unknown mid"
    (let [patient (mock-request-patient-get-by-mid "unknownmid32")]
      (patient-doesnt-exist? patient))))

(deftest ^:test-x patient-search
  (println 'RUN-PATIENT-SEARCH)
  (let [jackie-chan-new-york {:first-name "Jackie"
                              :last-name "Chan"
                              :gender "male"
                              :birth-day 13
                              :birth-month 2
                              :birth-year 1930
                              :city "        New York"
                              :street "Big apple"
                              :house 20
                              :mid "111111111111"}
        rose-chan-new-york {:first-name "Rose"
                            :last-name "Chan"
                            :gender "Female"
                            :birth-day 2
                            :birth-month 10
                            :birth-year 1932
                            :city "        New York"
                            :street "Big apple"
                            :house 20
                            :mid "111111111112"}
        santa-claus-miami {:first-name "Santa"
                           :last-name "Claus"
                           :gender "Male"
                           :birth-day 3
                           :birth-month 1
                           :birth-year 1939
                           :city "Miami"
                           :street "Beach"
                           :house 215
                           :mid "333111111113"}
        jackie-chan-boston {:first-name "Jackie"
                            :last-name "Chan"
                            :gender "Male"
                            :birth-day 13
                            :birth-month 2
                            :birth-year 1930
                            :city "Boston"
                            :street "Flow"
                            :house 26
                            :mid "333111111114"}]
    (mock-request-patient-add jackie-chan-new-york)
    (mock-request-patient-add rose-chan-new-york)
    (mock-request-patient-add santa-claus-miami)
    (mock-request-patient-add jackie-chan-boston)

    (testing "Search 1, all params"
      (let [patients-found (mock-request-patient-search
                            (str "first-name=JacKIe&last-name=Chan&gender=Male&"
                                 "city=New%20york&age-bottom=92&age-top=93"
                                 "&mid=111111111111&offset=0"))]
        (found-patient-equals? patients-found jackie-chan-new-york)))

    (testing "Search all from one city"
      (let [patients-found (mock-request-patient-search "city=New%20york")]
        (is (patients-count-equals? patients-found 2))))

    (testing "Search last-name"
      (let [patients-found (mock-request-patient-search "last-name=Chan")]
        (is (patients-count-equals? patients-found 3))))

    (testing "Search no params"
      (let [patients-found (mock-request-patient-search "")]
        (is (patients-count-equals? patients-found 4))))

    (testing "Search mid"
      (let [patients-found (mock-request-patient-search (str "mid=" (:mid santa-claus-miami)))]
        (found-patient-equals? patients-found santa-claus-miami)))

    (testing "Search unknown mid"
      (let [patients-found (mock-request-patient-search "mid=34jaf349jasl")]
        (is (empty? patients-found))))

    (testing "Search half mid"
      (let [patients-found (mock-request-patient-search "mid=333")]
        (is (patients-count-equals? patients-found 2))))

    (testing "Search half first-name, half last-name with spaces around"
      (let [patients-found (mock-request-patient-search "first-name=%20San&last-name=%20%20us%20")]
        (found-patient-equals? patients-found santa-claus-miami)))

    (testing "Search not valid mid"
      (let [patients-found (mock-request-patient-search "mid=333fsdfsdfsdfsfsd")]
        (is-validation-error? patients-found)))

    (testing "Search not valid first-name"
      (let [patients-found (mock-request-patient-search (str "first-name=mmidx333fsdfsdfsdfsfs"
                                                             "dmidx333fsdfsdfsfsdmidx333fsdfsdf"
                                                             "sdfsfsdidx333fsdfsdfsdfsfsdmidx33"
                                                             "3fsdfsdfsdfsfsdmidx333fsdfsdfsdfs"
                                                             "fsdmidx333fsdfsdfsdfsfsd"))]
        (is-validation-error? patients-found)))

    (testing "Search with offset 3"
      (let [patients-found (mock-request-patient-search "offset=3")]
        (found-patient-equals? patients-found jackie-chan-boston)))))
