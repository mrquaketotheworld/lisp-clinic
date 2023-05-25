(ns api.patient
  (:require
   [db.models.patient :as patient]
   [utils.validation.patient :as validation-patient]
   [utils.format.patient :as format-patient]
   [utils.format.message :as message :refer [PATIENT-EXISTS PATIENT-DOESNT-EXIST
                                             VALIDATION-ERROR]]))

(defn add-edit [request callback]
  (let [patient-form (:body request)
        formatted-patient-form (format-patient/format-patient-form patient-form)]
    (if (validation-patient/is-patient-form-valid? formatted-patient-form)
      (let [patient-row (patient/get-by-mid (:mid formatted-patient-form))]
        (callback patient-row formatted-patient-form))
      (message/error VALIDATION-ERROR))))

(defn add [request]
  (add-edit request (fn [patient-row formatted-patient-form]
                      (if patient-row
                        (message/error PATIENT-EXISTS)
                        (do (patient/add formatted-patient-form) (message/success))))))

(defn delete [request]
  (patient/delete (:mid (:path-params request)))
  (message/success))

(defn edit [request]
  (add-edit request (fn [patient-row formatted-patient-form]
                      (if patient-row
                        (do (patient/edit formatted-patient-form) (message/success))
                        (message/error PATIENT-DOESNT-EXIST)))))

(defn get-by-mid [request]
  (if-let [patient (patient/get-by-mid (:mid (:path-params request)))]
    (message/success patient)
    (message/error PATIENT-DOESNT-EXIST)))

(defn search [{patient-form :params}]
  (let [formatted-patient-form (format-patient/format-patient-search-form patient-form)]
    (if (validation-patient/is-patient-search-form-valid? formatted-patient-form)
      (message/success (patient/search formatted-patient-form))
      (message/error VALIDATION-ERROR))))

(defn get-all [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "get-all"})
