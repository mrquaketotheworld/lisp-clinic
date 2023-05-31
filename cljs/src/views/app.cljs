(ns views.app
  (:require [views.modal :refer [modal]]
            [views.header :refer [header]]
            [views.table :refer [table]]
            [views.pagination :refer [pagination]]))

(defn app []
  [:<>
   [header]
   [table]
   [modal]
   [pagination]])
