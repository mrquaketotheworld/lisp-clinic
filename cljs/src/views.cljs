(ns views) ; TOOD remove redundant CSS

(defn pagination []
  [:nav.pagination
   [:a.pagination-previous.is-disabled "Previous"]
   [:a.pagination-next "Next page"]
   [:ul.pagination-list
    [:li
     [:a.pagination-link.is-current 1]]
    [:li
     [:a.pagination-link 2]]
    [:li
     [:a.pagination-link 3]]]])

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
  [:div.modal.is-active
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "title"]
     [:button.delete]]
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
     [:button.button "Cancel"]]]])

(defn select-age [select-name default-option]
  [:div.control.has-icons-left
   [:div.select
    [:select {:name select-name :default-value default-option}
     (map (fn [i] [:option {:value i :key i} i]) (range 101))]]
   [:div.icon.is-small.is-left
    [:i.fa-solid.fa-list-ol]]])

(defn select-age-bottom []
  [select-age "age-bottom" "0"])

(defn select-age-top []
  [select-age "age-top" "100"])

(defn select-city []
  [:div.control.has-icons-left
   [:div.select
    [:select {:name "city"}
     [:option {:value ""} "All"]]]
   [:div.icon.is-small.is-left
    [:i.fa-solid.fa-globe]]])

(defn select-gender []
  [:div.field
   [:label.label "Gender"]
   [:div.control.has-icons-left
    [:div.select
     [:select {:name "gender"}
      [:option {:value ""} "All"]
      [:option {:value "Male"} "Male"]
      [:option {:value "Female"} "Female"]]]
    [:div.icon.is-small.is-left
     [:i.fa-regular.fa-person-circle-question]]]])

(defn header []
  [:<>
   [:div.level
    [:p.level-item
     [:img.logo {:src "images/lispclinic.png"}]]]
   [:div.columns.box
    [:div.column [select-gender]]
    [:div.column
     [:div.field [:label.label "Age"] [:div.field.is-grouped
                                       [select-age-bottom]
                                       [select-age-top]]]]
    [:div.column [:div.field [:label.label "City"] [select-city]]]
    [:div.column.is-two-fifths.is-align-self-flex-end
     [:div.field.is-grouped
      [:div.control.is-expanded.has-icons-left
       [:input.input {:type "search" :name "search"
                      :placeholder (str "John Doe, 381293...")}]
       [:div.icon.is-small.is-left
        [:i.fa-sharp.fa-solid.fa-keyboard]]]
      [:div.control
       [:button.button.is-primary [:span.icon.is-small
                                   [:i.fa-solid.fa-magnifying-glass]]
        [:span "Search"]]]]]]])

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
       [:th.has-text-right [:button.button.is-info.is-pull-right
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

(defn app []
  [:<>
   [header]
   [table]
   #_[modal]
   [pagination]])
