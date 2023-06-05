(ns core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [resource-response redirect]]
            [ring.middleware.resource :refer [wrap-resource]]
            [reitit.ring.middleware.exception :as exception]
            [db.init-tables :as init-tables]
            [reitit.ring :as ring]
            [api.patient :as patient]
            [api.address :as address]))

(def app
  (ring/ring-handler
   (ring/router
    [["/" {:get (fn [_] (resource-response "index.html" {:root "public"}))}]
     ["/api"
      ["/patient"
       ["/add" {:post patient/add}]
       ["/delete/:mid" {:delete patient/delete}]
       ["/edit" {:post patient/edit}]
       ["/get/:mid" {:get patient/get-by-mid}]
       ["/search" {:get patient/search}]]
      ["/address"
       ["/cities" {:get address/get-cities}]]]]
    {:data {:middleware [exception/exception-middleware]}})
   (ring/create-default-handler
    {:not-found (fn [_] (redirect "/"))})))

(def wrapped-app (->
                  #'app
                  (wrap-resource "public")
                  (wrap-json-body {:keywords? true})
                  wrap-json-response wrap-reload wrap-keyword-params wrap-params))

(defn -main []
  (try (init-tables/init) (catch Exception _))
  (run-jetty wrapped-app {:port 3000 :join? false}))
