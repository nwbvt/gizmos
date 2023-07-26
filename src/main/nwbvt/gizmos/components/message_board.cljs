(ns nwbvt.gizmos.components.message-board
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::messages
  (fn [db [_ id]]
    (let [messages (get-in db [::messages id] [])]
      messages)))

(defonce -message-counter (atom 0))

(defn -board-messages
  [db board-id]
  (or (get-in db [::messages board-id]) []))

(defn -add-message
  [db board-id message message-type]
  (let [curr-messages (-board-messages db board-id)
        new-messages (conj
                       curr-messages
                       {:type message-type
                        :text message
                        :id (swap! -message-counter inc)})]
    (assoc-in
      db
      [::messages board-id]
      new-messages)))

(rf/reg-event-db
  ::info
  (fn [db [_ board-id message]]
    (-add-message db board-id message :info)))

(rf/reg-event-db
  ::error
  (fn [db [_ board-id message]]
    (-add-message db board-id message :error)))

(rf/reg-event-db
  ::warn
  (fn [db [_ board-id message]]
    (-add-message db board-id message :warn)))

(defn -delete-message
  [db board-id message-id]
  (let [curr-messages (-board-messages db board-id)
        new-messages (remove #(= message-id (:id %))
                             curr-messages)]
    (assoc-in
      db
      [::messages board-id]
      new-messages)))

(rf/reg-event-db
  ::delete-message
  (fn [db [_ board-id message-id]]
    (-delete-message db board-id message-id)))

(defn message-board
  ([]
   (message-board :default))
  ([id]
   [:section.section
    (let [messages @(rf/subscribe [::messages id])]
      (for [message messages]
        [:div.notification {:key (:id message)
                            :class (case (:type message)
                                     :info "is-info is-light"
                                     :warn "is-warning is-light"
                                     :error "is-danger is-light")}
         [:button.delete {:on-click #(rf/dispatch [::delete-message id (:id message)])}]
         (:text message)]))]))
