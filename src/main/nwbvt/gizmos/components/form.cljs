(ns nwbvt.gizmos.components.form
  (:require [re-frame.core :as rf]
            [struct.core :as st]))

(rf/reg-sub
  ::forms
  (fn [db _]
    (::forms db)))

(rf/reg-sub
  ::form
  :<- [::forms]
  (fn [form [_ form-id]]
    (get form form-id)))

(rf/reg-sub
  ::field
  (fn [[_ form _]]
    (rf/subscribe [::form form]))
  (fn [form [_ _ field]]
    (get form field)))

(def ^:dynamic *schema* nil)

(rf/reg-event-fx
  ::form-submitted
  (fn [{db :db} [_ form schema submit-event]]
    (if (or (nil? schema) (st/validate schema form))
      (rf/dispatch [submit-event form]))))

(rf/reg-event-db
  ::update-field
  (fn [db [_ form field new-value]]
    (assoc-in db [::forms form field] new-value)))

(defn text-input
  "Text input"
  [field form & {:keys [label id options]}]
  (let [id (or id
               (if (nil? form) field
                 (str (name form) "/" (name field))))]
    [:div.field
     [:label.label {:for id} label]
     (let [value @(rf/subscribe [::field form field])]
       [:input.input (merge {:type :text :value value :id id
                             :on-change #(rf/dispatch [::update-field form field
                                                       (.. % -target -value)])}
                            options)])]))

(defn select-input
  "Select box"
  [field form & {:keys [label choices]}]
  [:div.field
   [:label.label label]
   (let [value @(rf/subscribe [::field form field])]
     [:div.control>div.select>select
      {:value value
       :on-change #(rf/dispatch [::update-field form field (.. % -target -value)])}
      (for [{:keys [value label]} choices]
        [:option {:key (or value label) :value value} label])])])

(defn submit-button
  "Submit button"
  [field & {:keys [label]}]
  [:div.field>div.control
   [:input.button.is-primary {:type :submit :value label}]])

(defn form
  "Defines a form"
  ([id submit-event fields]
   (form id nil submit-event fields))
  ([id schema submit-event fields]
   (let [value @(rf/subscribe [::form id])]
     [:form.form
      {:on-submit 
       (fn [e]
         (.preventDefault e)
         (rf/dispatch [::form-submitted value schema submit-event]))}
      (doall
        (for [[field-name {field-type :type :as field}] fields
              :let [control (case field-type
                              :text (text-input field-name id field)
                              :select (select-input field-name id field)
                              :submit (submit-button field-name id field)
                              (.log js/console (str "Cannot render field " field)))]
              :when control]
          [:div.field {:key field-name} control]))])))


