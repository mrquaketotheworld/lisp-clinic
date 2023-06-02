(ns views.table
  (:require [re-frame.core :as rf]
            [dispatches :refer [show-modal delete-patient]]))

(defn render-patients-rows [patients]
  (map-indexed (fn [i {:keys [firstname lastname birth city street house mid]}]
                 [:tr
                  {:key i}
                  [:th (inc i)]
                  [:td firstname]
                  [:td lastname]
                  [:td birth]
                  [:td (str city ", " street ", " house)]
                  [:td mid]
                  [:td.has-text-right
                   [:button.button.is-warning.mr-3
                    [:span.icon.is-small
                     [:i.fa-solid.fa-pen]]]
                   [:button.button.is-danger {:on-click #(delete-patient mid)}
                    [:span.icon.is-small
                     [:i.fa-solid.fa-xmark]]]]]) patients))

(defn table []
  (let [patients @(rf/subscribe [:patients])
        error-message @(rf/subscribe [:ajax-error])]
    [:<>
     (when error-message
       [:div.notification.is-danger
        [:button.delete] error-message])
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
         (render-patients-rows patients)]]]]]))

