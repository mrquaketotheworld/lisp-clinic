(ns core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [file-response]]
            [ring.middleware.file :refer [wrap-file]]
            [reitit.ring.middleware.exception :as exception]
            [db.init-tables :as init-tables]
            [reitit.ring :as ring]
            [api.patient :as patient]
            [config]
            [utils.file.interact :as file]))

(def app
  (ring/ring-handler
   (ring/router
    [["/api"
      ["/patient"
       ["/add" {:post patient/add}]
       ["/delete/:mid" {:delete patient/delete}]
       ["/edit" {:post patient/edit}]
       ["/get/:mid" {:get patient/get-by-mid}]
       ["/search" {:get patient/search}]]]]
    {:data {:middleware [exception/exception-middleware]}})
   (ring/create-default-handler
    {:not-found (fn [_] (file-response "public/index.html"))})))

(def wrapped-app (->
                  #'app
                  (wrap-file "public")
                  (wrap-json-body {:keywords? true})
                  wrap-json-response wrap-reload wrap-keyword-params wrap-params))

(defn -main [& args]
  (config/set-config! (file/load-edn "config.edn"))
  (if (= (first args) "init-tables")
    (init-tables/-main)
    (run-jetty wrapped-app {:port 3000 :join? false})))