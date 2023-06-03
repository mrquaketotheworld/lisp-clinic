(ns api.address
  (:require [db.models.address :as address]
            [utils.format.message :as message]))

(defn get-cities [_]
  (message/success (address/get-cities)))

