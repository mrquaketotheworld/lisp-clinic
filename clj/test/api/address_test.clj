(ns api.address-test
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [core]
            [fixtures]
            [utils :refer [mock-request-patient-add mock-request-get]]))

(deftest get-cities
  (println 'RUN-ADDRESS-GET-CITIES)

  (testing "Get unique cities"
    (mock-request-patient-add {:firstname "Liza"
                               :lastname "Simpson"
                               :gender "Female"
                               :birth "1994-10-30"
                               :city "New york"
                               :street "big apple"
                               :house "20"
                               :mid "123426782328"})
    (mock-request-patient-add {:firstname "Bart"
                               :lastname "Simpson"
                               :gender "Male"
                               :birth "1992-08-26"
                               :city "Los Angeles"
                               :street "Flow"
                               :house "22"
                               :mid "123426782329"})
    (mock-request-patient-add {:firstname "Homer"
                               :lastname "Simpson"
                               :gender "Male"
                               :birth "1955-11-30"
                               :city "   los angeles"
                               :street "big apple"
                               :house "20"
                               :mid "123426782330"})
    (is (= (mock-request-get "/api/address/cities") '("New York" "Los Angeles")))))

(use-fixtures :once fixtures/db-fixture)
(use-fixtures :each fixtures/clear-tables-fixture)
