(ns nwbvt.gizmos.components.modal
  (:require [re-frame.core :as rf]))

(def close-functions (atom {}))

(rf/reg-sub
  ::active-modal
  (fn [db]
    (::active-modal db)))

(rf/reg-event-db
  ::activate-modal
  (fn [db [_ modal]]
    (assoc db
           ::active-modal
           modal)))

(rf/reg-event-fx
  ::close-modal
  (fn [{db :db} [_ modal]]
    (let [on-close (@close-functions modal)]
      {:db (assoc db
                  ::active-modal
                  nil)
       :fx [(if on-close [:dispatch [on-close]])]})))

(defn launch-modal
  [id]
  (rf/dispatch [::activate-modal id]))

(defn close-modal
  [id]
  (rf/dispatch [::close-modal id]))

(defn modal
  [id & {:keys [title body footer on-close]}]
  (if on-close (swap! close-functions assoc id on-close))
  (let [close #(rf/dispatch [::close-modal id])]
    [:div.modal (if (= @(rf/subscribe [::active-modal]) id) {:class "is-active"})
     [:div.modal-background {:onClick close}]
     [:div.modal-card
      [:div.modal-card-head
       [:p.modal-card-title (if (> (count title 40))
                              (str (subs title 0 40) "...")
                              title)]
       [:button.delete {:aria-label "close" :onClick close}]]
      [:section.modal-card-body body]
      [:footer.modal-card-foot footer]]]))
