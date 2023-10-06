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
  ::set-schema
  (fn [db [_ form schema]]
    (assoc-in db [::schemas form] schema)))

(defn field-id
  [form field id]
  (or id
      (if (nil? form) field
        (str (name form) "/" (name field)))))

(defn text-input
  "Text input"
  [field form & {:keys [label id options]}]
  (let [id (field-id form field id)]
    [:div.field
     [:label.label {:for id} label]
     (let [value @(rf/subscribe [::field form field])
           error @(rf/subscribe [::field-error form field])]
       [:div
        (if (nil? error) [:span]
         [:div.notification.is-danger error])
        [:input.input (merge {:type :text :value value :id id
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
          [:div.notification.is-danger error])
        [:div.control>div.select>select
        {:id id
         :on-change #(rf/dispatch [::update-field form field (.. % -target -value)])}
        (for [{:keys [value label]} choices]
          [:option {:key (or value label) :value value} label])]])]))

(defn submit-button
  "Submit button"
  [field form & {:keys [label]}]
  (let [errors @(rf/subscribe [::errors form])]
    [:div.field>div.control
     [:input.button.is-primary {:type :submit :value label
                                :disabled (not (nil? errors))}]]))

(defn form
  "Defines a form"
  ([id fields submit-event schema]
   (rf/dispatch [::set-schema id schema])
   (form id fields submit-event))
  ([id fields submit-event]
   (let [[errors value] @(rf/subscribe [::validated-form id])]
     [:form.form
      {:on-submit
       (fn [e]
         (.preventDefault e)
         (if (nil? errors) (rf/dispatch [submit-event value])))}
      (doall
        (for [[field-name {field-type :type :as field}] fields
              :let [control (case field-type
                              :text (text-input field-name id field)
                              :select (select-input field-name id field)
                              :submit (submit-button field-name id field)
                              (.log js/console (str "Cannot render field " field)))]
              :when control]
          [:div.field {:key field-name} control]))])))


