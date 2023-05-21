(ns core
  (:gen-class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [db.init-tables :as init-tables]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World, again!"})

(def wrapped-handler (-> #'handler wrap-reload))

(defn -main [& args]
  (if (= (first args) "init-tables")
    (init-tables/-main)
    (run-jetty wrapped-handler {:port 3000
                                :join? false})))
