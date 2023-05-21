(ns core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [db.init-tables :as init-tables]
            [reitit.ring :as ring]))

(defn handler [request]
  (println (:path-params request))
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World, again!"})

(def wrapped-handler (-> #'handler wrap-reload))

(def app
  (ring/ring-handler
    (ring/router
      [["/" wrapped-handler]
       ["/hello/:id" wrapped-handler]])))

(defn -main [& args]
  (if (= (first args) "init-tables")
    (init-tables/-main)
    (run-jetty app {:port 3000 :join? false})))
