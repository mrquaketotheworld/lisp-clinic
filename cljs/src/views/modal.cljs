(ns views.modal
  (:require [re-frame.core :as rf]
            [dispatches :refer [hide-modal on-patient-form-change trim-form]]
            [utils.format.time :as time]))

(defn on-input-change [field-key]
  #(on-patient-form-change field-key (.. % -target -value)))

(defn on-form-submit [event]
  (.preventDefault event))

(defn input [field-key label input-name placeholder classes]
  [:div.column
   [:label.label label]
   [:div.control.is-expanded.has-icons-left
    [:input.input {:type "text" :name input-name
                   :placeholder placeholder
                   :value (field-key @(rf/subscribe [:patient]))
                   :required true
                   :max-length 128
                   :on-change (on-input-change field-key)}]
    [:div.icon.is-small.is-left
     [:i {:class classes}]]]])

(defn select-gender-simple []
  [:div.column.gender-simple
   [:label.label "Gender"]
   [:div.control.has-icons-left
    [:div.select
     [:select {:name "gender" :value (:gender @(rf/subscribe [:patient]))
               :on-change (on-input-change :gender)}
      [:option {:value "Male"} "Male"]
      [:option {:value "Female"} "Female"]]]
    [:div.icon.is-small.is-left
     [:i.fa-regular.fa-person-circle-question]]]])

(defn modal []
  [:form
   {:on-submit on-form-submit :on-blur #(trim-form)}
   [:div.modal {:class (when @(rf/subscribe [:modal-active?]) "is-active")}
    [:div.modal-background]
    [:div.modal-card
     [:header.modal-card-head
      [:p.modal-card-title "title"]
      [:button.delete {:on-click hide-modal}]]
     [:section.modal-card-body
      [:div.columns
       [input :firstname "First Name" "firstname" "Homer" "fa-solid fa-address-card"]
       [input :lastname "Last Name" "lastname" "Simpson" "fa-solid fa-address-card"]]
      [:div.columns
       [select-gender-simple]
       [:div.column
        [:label.label "Birth"]
        [:input.input {:type "date" :min "1900-01-01" :max (time/current-date) :required true
                       :on-change (on-input-change :date)}]]
       [input :mid "Medical Insurance ID" "mid" "342929393329" "fa-solid fa-file-medical"]]
      [:div.columns
       [input :city "City" "city" "New York" "fa-solid fa-globe"]
       [input :street "Street" "street" "Park Avenue" "fa-solid fa-road"]
       [input :house "House" "house" "332" "fa-solid fa-house"]]]
     [:footer.modal-card-foot
      [:button.button.is-success "Saved"]
      [:button.button {:on-click hide-modal} "Cancel"]]]]])


