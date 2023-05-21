(ns core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [db.init-tables :as init-tables]
            [reitit.ring :as ring]
            [api.patient :as patient]))

(def app
  (ring/ring-handler
   (ring/router
    [["/api"
      ["/patient"
       ["/add" {:post patient/add}]
       ["/delete" {:delete patient/delete}]
       ["/edit" {:post patient/edit}]
       ["/get" {:get patient/get}]
       ["/search" {:get patient/search}]
       ["/get-all" {:get patient/get-all}]]]])
   (ring/create-default-handler
    {:not-found (constantly {:status 404, :body "Oops... Not found"})})))

(def wrapped-app (-> #'app wrap-reload)) ; TODO fix before prod

(defn -main [& args]
  (if (= (first args) "init-tables")
    (init-tables/-main)
    (run-jetty wrapped-app {:port 3000 :join? false})))
