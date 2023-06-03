(ns utils
  (:require [cheshire.core :as json]
            [ring.mock.request :as mock]
            [core]))

(defn json-parse-body [body]
  (json/parse-string body true))

(defn mock-request [request-type url]
  (-> (mock/request request-type url)
      core/wrapped-app
      :body
      json-parse-body))

(defn mock-request-get [url]
  (mock-request :get url))

(defn mock-request-post [url body]
  (-> (mock/request :post url)
      (mock/json-body body)
      core/wrapped-app
      :body
      json-parse-body))

(defn mock-request-patient-add [body]
  (mock-request-post "/api/patient/add" body))

(defn mock-request-delete [mid]
  (mock-request :delete (str "/api/patient/delete/" mid)))

(defn mock-request-patient-get-by-mid [mid]
  (-> (str "/api/patient/get/" mid)
      mock-request-get))

(defn mock-request-patient-search [query-string]
  (mock-request-get (str "/api/patient/search?" query-string)))

(defn mock-request-patient-edit [body]
  (mock-request-post "/api/patient/edit" body))

(defn mock-request-get-cities []
  (mock-request-get "/api/address/cities"))
