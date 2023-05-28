(ns utils.format.message)

(def APPLICATION-JSON {"Content-Type" "application/json"})
(def PATIENT-DOESNT-EXIST "Patient doesn't exist")
(def VALIDATION-ERROR "Validation error")
(def PATIENT-EXISTS "Patient already exists")

(defn error [value]
  {:status 400
   :headers APPLICATION-JSON
   :body {:error value}})

(defn success
  ([] (success {:success true}))
  ([body]
   {:status 200
    :headers APPLICATION-JSON
    :body body}))
