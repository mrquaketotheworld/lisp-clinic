(ns events
  (:require [re-frame.core :as rf]
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

(rf/reg-event-db
 :init-db
 check-spec-interceptor
 (fn []
   {:modal-active? false
    :patient default-patient}))

(rf/reg-event-db
 :modal-active?
 check-spec-interceptor
 (fn [db event]
   (assoc db :modal-active? (second event))))

(rf/reg-event-fx
 :search-patients
 check-spec-interceptor
 (fn []
   {:http-xhrio {:method :get
                 :uri "/api/patient/search"
                 :response-format (ajax/json-response-format {:keywords? true})
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
   (assoc db :ajax-error (or (:error (:response response)) DEFAULT-ERROR-MESSAGE))))

(rf/reg-event-fx
 :delete-patient
 check-spec-interceptor
 (fn [_ [_ mid]]
   {:http-xhrio {:method :delete
                 :uri (str "/api/patient/delete/" mid)
                 :body {}
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:on-delete-patient-success mid]
                 :on-failure [:ajax-error]}}))

(rf/reg-event-db
 :fill-edit-patient
 check-spec-interceptor
 (fn [db [_ mid]]
   (assoc-in db [:patient] (first (filter #(= mid (:mid %)) (:patients db))))))

(rf/reg-event-db
 :on-delete-patient-success
 check-spec-interceptor
 (fn [db [_ mid]]
   (assoc db :patients (filter #(not= (:mid %) mid) (:patients db)))))

(rf/reg-event-db
 :patient-form-change
 check-spec-interceptor
 (fn [db [_ field-key value]]
   (assoc-in db [:patient field-key] value)))

(rf/reg-event-db
 :trim-form
 check-spec-interceptor
 (fn [db]
   (assoc db :patient
          (reduce (fn [acc key-value]
                    (assoc acc (first key-value) (.trim (second key-value)))) {} (:patient db)))))

(rf/reg-event-fx
 :add-patient
 check-spec-interceptor
 (fn [{:keys [db]}]
   {:http-xhrio {:method :post
                 :uri "/api/patient/add"
                 :response-format (ajax/json-response-format {:keywords? true})
                 :format (ajax/json-request-format)
                 :params (:patient db)
                 :on-success [:on-add-patient-success]
                 :on-failure [:ajax-error]}}))

(rf/reg-event-db
 :on-add-patient-success
 check-spec-interceptor
 (fn [db [_ response]]
   (-> db
       (update-in [:patients] #(into [(:patient response)] %))
       (assoc :ajax-success (:message response)))))

(rf/reg-event-db
 :clear-patient
 check-spec-interceptor
 (fn [db]
   (assoc db :patient default-patient)))
