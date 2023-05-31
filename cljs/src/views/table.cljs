(ns views.table
  (:require [dispatches :refer [show-modal]]))

(defn table []
  [:div.columns.box.mt-4
   [:div.column
    [:table.table
     [:thead
      [:tr
       [:th "#"]
       [:th "Name"]
       [:th "Gender"]
       [:th "Birth"]
       [:th "Address"]
       [:th "MID"]
       [:th.has-text-right
        [:button.button.is-info.is-pull-right {:on-click show-modal}
         [:span.icon.is-small
          [:i.fa-solid.fa-user-plus]]
         [:span "Add patient"]]]]]
     [:tbody
      [:tr
       [:th 1]
       [:td "Homer Simpson"]
       [:td "Male"]
       [:td "05-25-1956"]
       [:td "New York, Green street, 35"]
       [:td "34324jj32322"]
       [:td.has-text-right
        [:button.button.is-warning.mr-3
         [:span.icon.is-small
          [:i.fa-solid.fa-pen]]]
        [:button.button.is-danger
         [:span.icon.is-small
          [:i.fa-solid.fa-xmark]]]]]]]]])

