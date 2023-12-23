(ns nwbvt.gizmos.components.form
  (:require [re-frame.core :as rf]
            [struct.core :as st]))

(rf/reg-sub
  ::forms
  (fn [db _]
    (::forms db)))

(rf/reg-sub
  ::schemas
  (fn [db _]
    (::schemas db)))

(rf/reg-sub
  ::validated-form
  :<- [::forms]
  :<- [::schemas]
  (fn [[forms schemas] [_ id]]
    (let [form (get forms id)
          schema (get schemas id)]
      (if (nil? schema)
        [nil form]
        (st/validate form schema)))))

(rf/reg-sub
  ::form
  :<- [::forms]
  (fn [forms [_ id]]
    (get forms id)))

(rf/reg-sub
  ::errors
  (fn [[_ id]]
    (rf/subscribe [::validated-form id]))
  (fn [[errors _] _]
    errors))

(rf/reg-sub
  ::field-error
  (fn [[_ form _]]
    (rf/subscribe [::errors form]))
  (fn [errors [_ _ field]]
    (get errors field)))

(rf/reg-sub
  ::field
  (fn [[_ form _]]
    (rf/subscribe [::form form]))
  (fn [form [_ _ field]]
    (get form field)))

(rf/reg-event-db
  ::update-field
  (fn [db [_ form field new-value]]
    (assoc-in db [::forms form field] new-value)))

(rf/reg-event-db
  ::populate-form
  (fn [db [_ id value]]
    (assoc-in db [::forms id] value)))

(rf/reg-event-db
  ::set-schema
  (fn [db [_ form schema]]
    (assoc-in db [::schemas form] schema)))

(defn field-id
  [form field id]
  (or id
      (if (nil? form) field
        (str (name form) "-" (name field)))))

(defn basic-input
  "Text input"
  [field form input-type & {:keys [label id options]}]
  (let [id (field-id form field id)]
    [:div.field
     [:label.label {:for id} label]
     (let [value @(rf/subscribe [::field form field])
           error @(rf/subscribe [::field-error form field])]
       [:div
        (if (nil? error) [:span]
         [:div.notification.is-danger {:id (str id "-error")} error])
        [:input.input (merge {:type input-type :value value :id id
                              :on-change #(rf/dispatch [::update-field form field
                                                        (.. % -target -value)])}
                             options)]])]))

(defn select-input
  "Select box"
  [field form & {:keys [id label choices]}]
  (let [id (field-id form field id)]
    [:div.field
     [:label.label {:for id} label]
     (let [value @(rf/subscribe [::field form field])
           error @(rf/subscribe [::field-error form field])]
       [:div
        (if (nil? error) [:span]
          [:div.notification.is-danger {:id (str id "-error")} error])
        [:div.control>div.select>select
        {:id id
         :default-value value
         :on-change #(rf/dispatch [::update-field form field (.. % -target -value)])}
        (for [{:keys [value label]} choices]
          [:option {:key (or value label) :value value} label])]])]))

(defn checkbox-input
  "Checkbox"
  [field form & {:keys [id label]}]
  (let [id (field-id form field id)]
    [:div.field
     (let [value @(rf/subscribe [::field form field])
           error @(rf/subscribe [::field-error form field])]
       [:div
        (if (nil? error) [:span]
          [:div.notification.is-danger {:id (str id "-error")} error])
        [:label.checkbox
         [:input {:type :checkbox
                  :id id
                  :checked value
                  :on-change #(rf/dispatch [::update-field form field (.. % -target -checked)])}]
         label]])]))

(defn submit-button
  "Submit button"
  [field form & {:keys [label id]}]
  (let [errors @(rf/subscribe [::errors form])
        id (field-id form field id)]
    [:div.field>div.control
     [:input.button.is-primary {:type :submit :value label
                                :id id :form form
                                :disabled (not (nil? errors))}]]))

(defn form
  "Defines a form"
  ([id fields submit-event schema]
   (rf/dispatch [::set-schema id schema])
   (form id fields submit-event))
  ([id fields submit-event]
   (let [[errors value] @(rf/subscribe [::validated-form id])]
     [:form.form
      {:id id
       :on-submit
       (fn [e]
         (.preventDefault e)
         (if (nil? errors) (rf/dispatch [submit-event value])))}
      (doall
        (for [[field-name {field-type :type :as field}] fields
              :let [control (case field-type
                              :select (select-input field-name id field)
                              :checkbox (checkbox-input field-name id field)
                              :submit (submit-button field-name id field)
                              (basic-input field-name id field-type field))]
              :when control]
          [:div.field {:key field-name} control]))])))

(defn populate-form
  "Populate the values of a form"
  [id values]
  (rf/dispatch [::populate-form id values]))
