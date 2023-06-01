(ns views.modal
  (:require [re-frame.core :as rf]
            [dispatches :refer [hide-modal]]))

(defn input [label input-name placeholder classes]
  [:div.column
   [:label.label label]
   [:div.control.is-expanded.has-icons-left
    [:input.input {:type "text" :name input-name
                   :placeholder (str placeholder)}]
    [:div.icon.is-small.is-left
     [:i {:class classes}]]]])

(defn select-gender-simple []
  [:div.column.gender-simple
   [:label.label "Gender"]
   [:div.control.has-icons-left
    [:div.select
     [:select {:name "gender"}
      [:option {:value "Male"} "Male"]
      [:option {:value "Female"} "Female"]]]
    [:div.icon.is-small.is-left
     [:i.fa-regular.fa-person-circle-question]]]])

(defn modal []
  [:div.modal {:class (when @(rf/subscribe [:modal-active?]) "is-active")}
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "title"]
     [:button.delete {:on-click hide-modal}]]
    [:section.modal-card-body
     [:div.columns
      [input "First Name" "first-name" "Homer" "fa-solid fa-address-card"]
      [input "Last Name" "last-name" "Simpson" "fa-solid fa-address-card"]]
     [:div.columns
      [select-gender-simple]
      [:div.column
       [:label.label "Birth"]
       [:input.input {:type "date" :min "1900-01-01" :max (-> (js/Date.)
                                                              .toISOString
                                                              (.split "T")
                                                              first)}]]
      [input "Medical Insurance ID" "mid" "342929393329" "fa-solid fa-file-medical"]]
     [:div.columns
      [input "City" "city" "New York" "fa-solid fa-globe"]
      [input "Street" "street" "Park Avenue" "fa-solid fa-road"]
      [input "House" "house" "332" "fa-solid fa-house"]]]
    [:footer.modal-card-foot
     [:button.button.is-success "Saved"]
     [:button.button {:on-click hide-modal} "Cancel"]]]])


