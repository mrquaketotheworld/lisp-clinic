(ns events
  (:require [re-frame.core :as rf]
            [clojure.string :as string]
            [ajax.core :as ajax]
            [db :refer [check-spec-interceptor]]))

(def DEFAULT-ERROR-MESSAGE "Oops... Sorry, something went wrong, try to reload the page")

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

(rf/reg-event-db
 :init-db
 check-spec-interceptor
 (fn []
   {:modal-active? false
    :patient-form-mode "add"
    :patient default-patient
    :filter-search default-filter-search
    :cities []
    :patients []}))

(rf/reg-event-db
 :modal-active?
 check-spec-interceptor
 (fn [db event]
   (assoc db :modal-active? (second event))))

(rf/reg-event-fx
 :search-patients
 check-spec-interceptor
 (fn [{:keys [db]}]
   {:http-xhrio {:method :get
                 :uri "/api/patient/search"
                 :params (into {} (filter #(not= (second %) "") (:filter-search db)))
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format (ajax/url-request-format)
                 :on-success [:on-search-patients-success]
                 :on-failure [:ajax-error]}}))

(rf/reg-event-db
 :on-search-patients-success
 check-spec-interceptor
 (fn [db [_ patients]]
   (assoc db :patients patients)))

(rf/reg-event-db
 :remove-ajax-success
 check-spec-interceptor
 (fn [db]
   (dissoc db :ajax-success)))

(rf/reg-event-db
 :remove-ajax-error
 check-spec-interceptor
 (fn [db]
   (dissoc db :ajax-error)))

(rf/reg-event-db
 :ajax-error
 check-spec-interceptor
 (fn [db [_ response]]
   (assoc db :ajax-error (str (or (:error (:response response)) DEFAULT-ERROR-MESSAGE)))))

(rf/reg-event-fx
 :delete-patient
 check-spec-interceptor
 (fn [_ [_ mid]]
   {:http-xhrio {:method :delete
                 :uri (str "/api/patient/delete/" mid)
                 :body {}
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:on-add-edit-delete-patient-success]
                 :on-failure [:ajax-error]}}))

(rf/reg-event-db
 :form-change
 check-spec-interceptor
 (fn [db [_ form field-key value]]
   (assoc-in db [form field-key] value)))

(rf/reg-event-db
 :trim-form
 check-spec-interceptor
 (fn [db [_ field-key]]
   (assoc db field-key
          (reduce (fn [acc key-value]
                    (let [field-key (first key-value)
                          value (.trim (second key-value))]
                      (if (= field-key :mid)
                        (assoc acc field-key (string/replace value #"\W" ""))
                        (assoc acc field-key value)))) {} (field-key db)))))

(rf/reg-event-fx
 :add-edit-patient
 check-spec-interceptor
 (fn [{:keys [db]}]
   {:http-xhrio {:method :post
                 :uri (str "/api/patient/" (:patient-form-mode db))
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format (ajax/json-request-format)
                 :params (:patient db)
                 :on-success [:on-add-edit-delete-patient-success]
                 :on-failure [:ajax-error]}}))

(rf/reg-event-fx
 :on-add-edit-delete-patient-success
 check-spec-interceptor
 (fn [{:keys [db]} [_ response]]
   {:db (assoc db :ajax-success (:message response))
    :fx [[:dispatch [:search-patients]]
         [:dispatch [:get-cities]]]}))

(rf/reg-event-db
 :patient-form-mode
 check-spec-interceptor
 (fn [db [_ mode]]
   (assoc db :patient-form-mode mode)))

(rf/reg-event-fx
 :get-cities
 check-spec-interceptor
 (fn []
   {:http-xhrio {:method :get
                 :uri "/api/address/cities"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:on-get-cities-success]
                 :on-failure [:ajax-error]}}))

(rf/reg-event-db
 :on-get-cities-success
 check-spec-interceptor
 (fn [db [_ cities]]
   (assoc db :cities cities)))

(rf/reg-event-fx
 :add-patient-form
 check-spec-interceptor
 (fn [{:keys [db]}]
   {:db (assoc db :patient default-patient)
    :fx [[:dispatch [:patient-form-mode "add"]]
         [:dispatch [:modal-active? true]]]}))

(rf/reg-event-fx
 :edit-patient-form
 check-spec-interceptor
 (fn [{:keys [db]} [_ mid]]
   {:db (assoc-in db [:patient] (first (filter #(= mid (:mid %)) (:patients db))))
    :fx [[:dispatch [:patient-form-mode "edit"]]
         [:dispatch [:modal-active? true]]]}))

