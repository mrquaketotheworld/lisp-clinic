(ns views.table
  (:require [re-frame.core :as rf]
            [dispatches :refer [show-modal]]))

(defn render-patients-rows [patients]
  (map-indexed (fn [i patient]
                 [:tr
                  {:key i}
                  [:th i]
                  [:td (:first_name patient)]
                  [:td (:last_name patient)]
                  [:td (:birth patient)]
                  [:td (str (:city patient) ", " (:street patient) ", " (:house patient))]
                  [:td (:mid patient)]
                  [:td.has-text-right
                   [:button.button.is-warning.mr-3
                    [:span.icon.is-small
                     [:i.fa-solid.fa-pen]]]
                   [:button.button.is-danger
                    [:span.icon.is-small
                     [:i.fa-solid.fa-xmark]]]]]) patients))

(defn table []
  (let [loading? @(rf/subscribe [:loading?])
        patients @(rf/subscribe [:patients])
        error-message @(rf/subscribe [:patients-fetch-error])]
    (if error-message
      [:h1 error-message]
      (if loading?
        [:progress.progress.is-small.is-primary]
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
            (render-patients-rows patients)]]]]))))

