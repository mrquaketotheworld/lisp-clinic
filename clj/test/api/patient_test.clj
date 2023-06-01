(ns api.patient-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [next.jdbc :as jdbc]
            [ring.mock.request :as mock]
            [cheshire.core :as json]
            [core]
            [config :as config-src-dir]
            [db.init-tables :as init-tables]
            [utils.validation.error :as error]
            [utils.format.patient :as patient-format]))

(def db-test {:dbtype "postgresql"
              :dbname "postgres"
              :host "localhost"
              :user "postgres"
              :password "postgres"})

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
  (is (= (count patients) value)))

(defn patient-equals? [patient-found patient]
  (is (= patient-found (patient-format/format-patient-form patient))))

(defn found-patients-equals? [patients-found patient]
  (patient-equals? (first patients-found) patient)
  (patients-count-equals? patients-found 1))

(defn equals-error? [message error]
  (is (= message (:error error))))

(defn patient-doesnt-exist? [patient]
  (equals-error? (:patient-doesnt-exist error/errors) patient))

(defn db-fixture [test-run]
  (config-src-dir/set-config! db-test) ; make global TEST configuration
  (jdbc/execute-one! db-test ["DROP TABLE IF EXISTS address, gender, patient"])
  (init-tables/-main)
  (test-run))
(use-fixtures :once db-fixture)

(defn clear-tables-fixture [test-run]
  (jdbc/execute! db-test ["DELETE FROM patient;
                           DELETE FROM address"])
  (test-run))
(use-fixtures :each clear-tables-fixture)

(deftest patient-add
  (println 'RUN-PATIENT-ADD)

  (testing "Patient was saved formatted"
    (let [homer-simpson {:firstname "Homer"
                         :lastname "Simpson"
                         :gender "      male    "
                         :birth "1965-12-25"
                         :city "        New YorK"
                         :street "Big apple       "
                         :house "20A"
                         :mid  "123426782326"}]
      (mock-request-patient-add homer-simpson)
      (let [patient (mock-request-patient-get-by-mid (:mid homer-simpson))]
        (patient-equals? patient homer-simpson))))

  (testing "Patient gets existing address"
    (let [city "New York" street "Yellow" house "22C"
          bart-simpson {:firstname "Bart"
                        :lastname "Simpson"
                        :gender "male"
                        :birth "1989-11-20"
                        :city city
                        :street street
                        :house house
                        :mid "123426782327"}]
      (mock-request-patient-add {:firstname "Santa"
                                 :lastname "Helper"
                                 :gender "male"
                                 :birth "1999-01-02"
                                 :city city
                                 :street street
                                 :house house
                                 :mid "023426782327"})
      (mock-request-patient-add bart-simpson)
      (let [patient (mock-request-patient-get-by-mid (:mid bart-simpson))]
        (patient-equals? patient bart-simpson))))

  (testing "Patient with the same mid already exists"
    (let [patient {:firstname "Anonymous"
                   :lastname "Simpson"
                   :gender "male"
                   :birth "1999-11-20"
                   :city "New york"
                   :street "big apple"
                   :house "20"
                   :mid "153426782327"}]
      (mock-request-patient-add patient)
      (let [body (mock-request-patient-add patient)]
        (equals-error? (:patient-exists error/errors) body))))

  (testing "Patient was saved"
    (let [body (mock-request-patient-add {:firstname "Liza"
                                          :lastname "Simpson"
                                          :gender "Female"
                                          :birth "1994-10-30"
                                          :city "New york"
                                          :street "big apple"
                                          :house "20"
                                          :mid "123426782328"})]
      (is (:success body))))

  (testing "Validation, patient without field"
    (let [body (mock-request-patient-add {:firstname "Liza"
                                          :lastname "Simpson"
                                          :birth "1994-10-30"
                                          :city "New york"
                                          :street "big apple"
                                          :house "20B"
                                          :mid "123426782328"})]
      (is (= {:error {:keys-missing-or-not-valid ["firstname" "lastname" "birth" "city" "street"
                                                   "house" "mid"]}} body)))))

(deftest patient-delete
  (println 'RUN-PATIENT-DELETE)

  (testing "Patient was deleted"
    (let [mid "123426782329"]
      (mock-request-patient-add {:firstname "Marge"
                                 :lastname "Simpson"
                                 :gender "Female"
                                 :birth "1970-07-19"
                                 :city "New york"
                                 :street "big apple"
                                 :house "20"
                                 :mid mid})
      (let [body (mock-request-delete mid)
            patient (mock-request-patient-get-by-mid mid)]
        (is (:success body))
        (patient-doesnt-exist? patient)))))

(deftest patient-edit
  (println 'RUN-PATIENT-EDIT)

  (testing "Patient was edited"
    (let [slim-shady-edit-patient-form {:mid "243283439393"
                                        :firstname "Slim"
                                        :lastname "Shady"
                                        :gender "Male"
                                        :city "Detroit"
                                        :street "Snow"
                                        :birth "1970-07-19"
                                        :house "25"}]
      (mock-request-patient-add {:firstname "Marshall"
                                 :lastname "Mather"
                                 :gender "Male"
                                 :birth "1970-07-19"
                                 :city "Compton"
                                 :street "Smith"
                                 :house "24"
                                 :mid (:mid slim-shady-edit-patient-form)})
      (mock-request-patient-edit slim-shady-edit-patient-form)
      (let [patient (mock-request-patient-get-by-mid (:mid slim-shady-edit-patient-form))]
        (patient-equals? patient slim-shady-edit-patient-form))))

  (testing "Patient doesn't exist"
    (let [body (mock-request-patient-edit {:firstname "John"
                                           :lastname "Chan"
                                           :gender "Male"
                                           :birth "1981-08-16"
                                           :city "Tokio"
                                           :street "Alex Yao"
                                           :house "2"
                                           :mid "423838383838"})]
      (patient-doesnt-exist? body))))

(deftest patient-get
  (println 'RUN-PATIENT-GET)

  (testing "Get patient by mid"
    (let [michael-moe {:mid "34239202023f"
                       :firstname "Michael"
                       :lastname "Moe"
                       :gender "Male"
                       :city "Boston"
                       :street "Flinstone"
                       :birth "1973-08-21"
                       :house "273"}]
      (mock-request-patient-add michael-moe)
      (let [patient (mock-request-patient-get-by-mid (:mid michael-moe))]
        (patient-equals? patient michael-moe))))

  (testing "Get patient by unknown mid"
    (let [patient (mock-request-patient-get-by-mid "unknownmid32")]
      (patient-doesnt-exist? patient))))

(deftest patient-search
  (println 'RUN-PATIENT-SEARCH)
  (let [jackie-chan-new-york {:firstname "Jackie"
                              :lastname "Chan"
                              :gender "male"
                              :birth "1930-02-13"
                              :city "        New York"
                              :street "Big apple"
                              :house "20"
                              :mid "111111111111"}
        rose-chan-new-york {:firstname "Rose"
                            :lastname "Chan"
                            :gender "Female"
                            :birth "1932-10-02"
                            :city "        New York"
                            :street "Big apple"
                            :house "20"
                            :mid "111111111112"}
        santa-claus-miami {:firstname "Santa"
                           :lastname "Claus"
                           :gender "Male"
                           :birth "1939-01-03"
                           :city "Miami"
                           :street "Beach"
                           :house "215"
                           :mid "333111111113"}
        jackie-chan-boston {:firstname "Jackie"
                            :lastname "Chan"
                            :gender "Male"
                            :birth "1930-02-13"
                            :city "Boston"
                            :street "Flow"
                            :house "26"
                            :mid "333111111114"}]
    (mock-request-patient-add jackie-chan-new-york)
    (mock-request-patient-add rose-chan-new-york)
    (mock-request-patient-add santa-claus-miami)
    (mock-request-patient-add jackie-chan-boston)

    (testing "Search 1, all params"
      (let [patients-found (mock-request-patient-search
                            (str "search=JacKIe%20Chan&gender=Male&"
                                 "city=New%20york&age-bottom=92&age-top=93&limit=1&offset=0"))]
        (found-patients-equals? patients-found jackie-chan-new-york)))

    (testing "Search firstname Rose and lastname Clause"
      (let [patients-found (mock-request-patient-search "search=rose%20cla%2022")]
        (patient-equals? (first patients-found) rose-chan-new-york)
        (patient-equals? (second patients-found) santa-claus-miami)
        (patients-count-equals? patients-found 2)))

    (testing "Search all from one city"
      (let [patients-found (mock-request-patient-search "city=New%20york")]
        (patients-count-equals? patients-found 2)))

    (testing "Search last-name"
      (let [patients-found (mock-request-patient-search "search=Chan")]
        (patients-count-equals? patients-found 3)))

    (testing "Search no params"
      (let [patients-found (mock-request-patient-search "")]
        (patients-count-equals? patients-found 4)))

    (testing "Search mid"
      (let [patients-found (mock-request-patient-search (str "search=" (:mid santa-claus-miami)))]
        (found-patients-equals? patients-found santa-claus-miami)))

    (testing "Search unknown mid"
      (let [patients-found (mock-request-patient-search "search=34jaf349jasl")]
        (is (empty? patients-found))))

    (testing "Search half mid"
      (let [patients-found (mock-request-patient-search "search=333")]
        (patients-count-equals? patients-found 2)))

    (testing "Search half first-name, half last-name with spaces around"
      (let [patients-found (mock-request-patient-search "search=%20San&%20%20us%20")]
        (found-patients-equals? patients-found santa-claus-miami)))

    (testing "Search not valid search"
      (let [patients-found (mock-request-patient-search (str "search=mmidx333fsdfsdfsdfsfs"
                                                             "dmidx333fsdfsdfsfsdmidx333fsdfsdf"
                                                             "sdfsfsdidx333fsdfsdfsdfsfsdmidx33"
                                                             "3fsdfsdfsdfsfsdmidx333fsdfsdfsdfs"
                                                             "fsdmidx333fsdfsdfsdfsfsd"))]
        (is (= {:error {:search "Max length is: 128"}} patients-found))))

    (testing "Search with offset 3"
      (let [patients-found (mock-request-patient-search "offset=3")]
        (found-patients-equals? patients-found santa-claus-miami)))))
