(ns views.table
  (:require [re-frame.core :as rf]
            [dispatches :refer [show-modal delete-patient remove-ajax-success remove-ajax-error
                                fill-edit-patient patient-form-mode clear-patient]]))

(defn on-edit-patient [mid]
  (patient-form-mode "edit")
  (fill-edit-patient mid)
  (show-modal))

(defn on-add-patient []
  (patient-form-mode "add")
  (clear-patient)
  (show-modal))

(defn render-patients-rows [patients]
  (map-indexed (fn [i {:keys [firstname lastname gender birth city street house mid]}]
                 [:tr
                  {:key i}
                  [:th (inc i)]
                  [:td (str firstname " " lastname)]
                  [:td gender]
                  [:td birth]
                  [:td (str city ", " street ", " house)]
                  [:td mid]
                  [:td.has-text-right
                   [:button.button.is-warning.mr-3 {:on-click #(on-edit-patient mid)}
                    [:span.icon.is-small
                     [:i.fa-solid.fa-pen]]]
                   [:button.button.is-danger {:on-click #(delete-patient mid)}
                    [:span.icon.is-small
                     [:i.fa-solid.fa-xmark]]]]]) patients))

(defn table []
  (let [patients @(rf/subscribe [:patients])
        error-message @(rf/subscribe [:ajax-error])
        success-message @(rf/subscribe [:ajax-success])]
    [:<>
     (when error-message
       [:div.notification.is-danger
        [:button.delete {:on-click remove-ajax-error}] error-message])
     (when success-message
       [:div.notification.is-success
        [:button.delete {:on-click remove-ajax-success}] success-message])
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
           [:button.button.is-info.is-pull-right {:on-click on-add-patient}
            [:span.icon.is-small
             [:i.fa-solid.fa-user-plus]]
            [:span "Add patient"]]]]]
        [:tbody
         (render-patients-rows patients)]]]]]))

