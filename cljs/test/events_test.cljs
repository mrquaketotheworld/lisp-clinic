(ns events-test
  (:require [cljs.test :refer-macros [deftest is]]
            [re-frame.core :as rf]
            [day8.re-frame.test :as rf-test]
            [events]
            [subs]))

(def test-temp "")
(def ERROR-MESSAGE "Oops... Sorry, something went wrong, try to reload the page")

(def default-patient {:firstname ""
                      :lastname ""
                      :gender "Male"
                      :birth ""
                      :mid ""
                      :city ""
                      :street ""
                      :house ""})

(def default-filter-search {:gender ""
                            :age-bottom "0"
                            :age-top "100"
                            :city ""
                            :search ""})

(def santa-claus {:firstname "Santa"
                  :lastname "Clause"
                  :gender "Male"
                  :birth "2023-05-31"
                  :city "Boston"
                  :street "Green"
                  :house "333"
                  :mid "111111111111"})

(def cities-example ["New York" "Boston"])
(def TEXT "Lorem ipsum")

(deftest main-test
  (rf-test/run-test-sync
   (rf/dispatch-sync [:init-db])
   (let [modal-active? (rf/subscribe [:modal-active?])
         patient-form-mode (rf/subscribe [:patient-form-mode])
         patient (rf/subscribe [:patient])
         filter-search (rf/subscribe [:filter-search])
         cities (rf/subscribe [:cities])
         patients (rf/subscribe [:patients])
         ajax-error (rf/subscribe [:ajax-error])
         ajax-success (rf/subscribe [:ajax-success])]

     (is (= false @modal-active?))
     (is (= "add" @patient-form-mode))
     (is (= default-patient @patient))
     (is (= default-filter-search @filter-search))
     (is (= [] @cities))
     (is (= [] @patients))

     (rf/dispatch [:ajax-error])
     (is (= ERROR-MESSAGE @ajax-error))

     (rf/dispatch [:remove-ajax-error])
     (is (not (contains? @ajax-error :ajax-error)))

     (rf/dispatch [:on-search-patients-success [santa-claus]])
     (is (= [santa-claus] @patients))

     (rf/dispatch [:edit-patient-form (:mid santa-claus)])
     (is (= "edit" @patient-form-mode))
     (is (= true @modal-active?))
     (is (= santa-claus @patient))

     (rf/dispatch [:add-patient-form])
     (is (= "add" @patient-form-mode))
     (is (= true @modal-active?))
     (is (= default-patient @patient))

     (rf/dispatch [:on-get-cities-success cities-example])
     (is (= cities-example @cities))

     (rf/dispatch [:on-add-edit-delete-patient-success {:message TEXT}])
     (is (= TEXT @ajax-success))

     (rf/dispatch [:remove-ajax-success])
     (is (not (contains? @ajax-error :ajax-success)))

     (rf/dispatch [:form-change :patient :firstname TEXT])
     (is (= TEXT (:firstname @patient)))

     (rf/dispatch [:form-change :filter-search :search TEXT])
     (is (= TEXT (:search @filter-search))))))
