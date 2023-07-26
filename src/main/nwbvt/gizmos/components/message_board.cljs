(ns nwbvt.gizmos.components.message-board
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::messages
  (fn [db [_ id]]
    (vals (get-in db [::messages id] []))))

(defonce -message-counter (atom 0))

(defn -board-messages
  [db board-id]
  (or (get-in db [::messages board-id]) []))

(defn -new-message
  [message-text message-type]
  (let [id (swap! -message-counter inc)]
    {:type message-type
     :text message-text
     :id id}))

(defn -add-message
  [db board-id message]
  (assoc-in db
            [::messages board-id (:id message)]
            message))

(defn -delete-message
  [db board-id message-id]
  (update-in db [::messages board-id] dissoc message-id))

(defn -fade-message
  [db board-id message-id fade-duration]
  (let [path [::messages board-id message-id]]
    (if (get-in db path)
      (update-in db path assoc :fade fade-duration)
      db)))

(defn -handle-add
  [db message-type [board-id message-text & {:keys [fade keep-for]}]]
  (let [message (-new-message message-text message-type)
        message-id (:id message)]
   {:db (-add-message db board-id message)
   :fx [(if fade
          [:dispatch-later
           {:ms (* 1000 (or keep-for 0))
            :dispatch [::fade-message board-id message-id fade]}])]}))

(rf/reg-event-fx
  ::info
  (fn [{db :db} event]
    (-handle-add db :info (rest event))))

(rf/reg-event-fx
  ::error
  (fn [{db :db} event]
    (-handle-add db :error (rest event))))

(rf/reg-event-fx
  ::warn
  (fn [{db :db} event]
    (-handle-add db :warn (rest event))))

(rf/reg-event-db
  ::delete-message
  (fn [db [_ board-id message-id]]
    (-delete-message db board-id message-id)))

(rf/reg-event-fx
  ::fade-message
  (fn [{db :db} [_ board-id message-id fade-duration]]
    {:db (-fade-message db board-id message-id fade-duration)
     :fx [[:dispatch-later {:ms (* 1000 fade-duration)
                            :dispatch [::delete-message board-id message-id]}]]}))

(defn message-board
  ([]
   (message-board :default))
  ([id]
   [:section.section
    (let [messages @(rf/subscribe [::messages id])]
      (for [message messages]
        [:div.notification {:key (:id message)
                            :id (str "message" (:id message))
                            :class (case (:type message)
                                     :info "is-info"
                                     :warn "is-warning"
                                     :error "is-danger")
                            :style (if (:fade message) {:opacity 0
                                                        :transition (str "opacity " (:fade message) "s linear")})}
         [:button.delete {:on-click #(rf/dispatch [::delete-message id (:id message)])}]
         (:text message)]))]))
