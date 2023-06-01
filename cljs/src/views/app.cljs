(ns views.app
  (:require [views.modal :refer [modal]]
            [views.header :refer [header]]
            [views.table :refer [table]]))

(defn app []
  [:<>
   [header]
   [table]
   [modal]])
