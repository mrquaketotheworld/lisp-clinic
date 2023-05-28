(ns config)

(def db-config {})

(defn set-config! [config]
  (alter-var-root (var db-config) (constantly config)))
