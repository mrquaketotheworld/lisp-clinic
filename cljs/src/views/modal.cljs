(ns views.modal
  (:require [re-frame.core :as rf]
            [dispatches :refer [hide-modal on-patient-form-change trim-form-patient
                                add-edit-patient]]
            [utils.format.time :as time]))

(defn on-input-change [field-key]
  #(on-patient-form-change field-key (.. % -target -value)))

(defn on-form-submit [event]
  (.preventDefault event)
  (add-edit-patient)
  (hide-modal))

(defn input [options-arrived]
  (let [defaults {:max-length 128}
        options (merge defaults options-arrived)
        input-name (:input-name options)
        keyword-input-name (keyword input-name)]
    [:div.column
     [:label.label (:label options)]
     [:div.control.is-expanded.has-icons-left
      [:input.input {:type "text" :name input-name
                     :placeholder (:placeholder options)
                     :value (keyword-input-name @(rf/subscribe [:patient]))
                     :required true
                     :pattern (:pattern options)
                     :title (:title options)
                     :max-length (:max-length options)
                     :disabled (:disabled options)
                     :on-change (on-input-change keyword-input-name)}]
      [:div.icon.is-small.is-left
       [:i {:class (:classes options)}]]]]))

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
     [:i.fa-solid.fa-person-circle-question]]]])

(defn modal []
  [:form
   {:method "POST" :on-submit on-form-submit :on-blur trim-form-patient}
   [:div.modal {:class (when @(rf/subscribe [:modal-active?]) "is-active")}
    [:div.modal-background]
    [:div.modal-card
     [:header.modal-card-head
      [:p.modal-card-title  (str @(rf/subscribe [:patient-form-mode]) " patient")]
      [:div.delete {:on-click hide-modal}]]
     [:section.modal-card-body
      [:div.columns
       [input {:label "First Name"
               :input-name "firstname"
               :placeholder "Homer"
               :classes "fa-solid fa-address-card"}]
       [input {:label "Last Name"
               :input-name "lastname"
               :placeholder "Simpson"
               :classes "fa-solid fa-address-card"}]]
      [:div.columns
       [select-gender-simple]
       [:div.column
        [:label.label "Birth"]
        [:input.input {:type "date" :min "1900-01-01" :max (time/current-date) :required true
                       :on-change (on-input-change :birth)
                       :value (:birth @(rf/subscribe [:patient]))}]]
       [input {:label "Medical Insurance ID"
               :input-name "mid"
               :placeholder "342A2f39a329"
               :pattern ".{12}"
               :title "MID length should be 12"
               :max-length 12
               :disabled (= @(rf/subscribe [:patient-form-mode]) "edit")
               :classes "fa-solid fa-id-card-clip"}]]
      [:div.columns
       [input {:label "City"
               :input-name "city"
               :placeholder "New York"
               :classes "fa-solid fa-tree-city"}]
       [input {:label "Street"
               :input-name "street"
               :placeholder "Park Avenue"
               :classes "fa-solid fa-road"}]
       [input {:label "House"
               :input-name "house"
               :placeholder "332A"
               :classes "fa-solid fa-house"}]]]
     [:footer.modal-card-foot
      [:button.button.is-success "Save"]
      [:div.button {:on-click hide-modal} "Cancel"]]]]])


