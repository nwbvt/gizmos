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

(def ^:dynamic *form* nil)

(rf/reg-event-fx
  ::form-submitted
  (fn [{db :db} [_ form schema submit-event]]
    (if (or (nil? schema) (st/validate schema form))
      (rf/dispatch [submit-event form]))))

(rf/reg-event-db
  ::update-field
  (fn [db [_ form field new-value]]
    (assoc-in db [::forms form field] new-value)))

(defn form
  "Defines a form"
  ([id schema submit-event content]
   (binding [*form* id]
     (let [value @(rf/subscribe [::form id])]
       [:form.form
        {:on-submit 
         (fn [e]
           (.preventDefault e)
           (rf/dispatch [::form-submitted value schema submit-event]))}
        (content)])))
  ([id submit-event content]
   (form id nil submit-event content)))

(defn text-input
  "Text input"
  [field label & {:keys [id options]}]
  (let [id (or id
               (if (nil? *form*) field
                 (str (name *form*) "/" (name field))))]
    [:div.field
     [:label.label {:for id} label]
     (let [form-id *form*
           value @(rf/subscribe [::field form-id field])]
       [:input.input (merge {:type :text :value value :id id
                             :on-change #(rf/dispatch [::update-field form-id field
                                                       (.. % -target -value)])}
                            options)])]))
