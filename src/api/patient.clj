(ns api.patient
  (:require
   [db.models.patient :as patient]
   [utils.validation.patient :as validation-patient]
   [utils.format.patient :as format-patient]
   [utils.format.message :as message]))

(defn add-edit [request callback]
  (let [patient-form (:body request)
        formatted-patient-form (format-patient/format-patient-form patient-form)]
    (if (validation-patient/is-patient-form-valid? formatted-patient-form)
      (let [patient-row (patient/get-by-mid (:mid formatted-patient-form))]
        (callback patient-row formatted-patient-form))
      (message/error "Validation error"))))

(defn add [request]
  (add-edit request (fn [patient-row formatted-patient-form]
                      (if patient-row
                        (message/error "Patient already exists")
                        (do (patient/add formatted-patient-form) (message/success))))))

(defn delete [request]
  (patient/delete (:mid (:body request)))
  (message/success))

(defn edit [request]
  (add-edit request (fn [patient-row formatted-patient-form]
                      (if patient-row
                        (do (patient/edit formatted-patient-form) (message/success))
                        (message/error "Patient doesn't exist")))))

(defn get-by-mid [request]
  (if-let [patient-row (patient/get-by-mid (:mid (:body request)))]
    (message/success patient-row)
    (message/error "Patient doesn't exist")))

(defn search [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "search"})

(defn get-all [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body "get-all"})
