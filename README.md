# gizmos
This is a library consisting of some components for a clojurescript Re-frame project using Bulma.

## Pager
The pager is a component to allow easy paging through a potentially long list of items.

### Usage
    (gizmos/pager pager-id last-page max-pages-to-show event-to-update-page)

The optional first argument is an id for the pager. This is useful for cases in which multiple pagers exist in the same app.

Next comes the page number for the last page, and the maximum number of pages to show at once. This cannot be below five. The first page, final page, current page, and previous and next pages will always be shown.

The final argument is the event that will be dispatched along with the page number when the page changes. This can be used to fetch the correct data for that page.

### Example
    (rf/reg-event-db
      ::change-page
      (fn [db [_ new-page]]
        (let [new-data (fetch-data new-page)]
            (assoc db ::displayed-data new-data))))

    (rf/reg-sub
      ::displayed-data
      (fn [db]
        (::displayed-data db)))

    (gizmos/pager 10 6 ::change-page)
    [:div.container
        (for [row @(rf/subscribe ::displayed-data)]
            (display-row row))]

## Modal
The modal is a simple dialog box that can be opened and closed.

### Usage
    (gizmos/modal modal-id :title modal-title
                           :body body-section
                           :footer footer-section
                           :on-close close-event)

The first argument is the modal id. It also takes in keyword arguments for the title, body section, footer section, and an event to be dispatched when the modal is closed.

The modal can be opened or closed by either dispatching the `::nwbvt.gizmos.components.modal.activate-modal` or `::nwbvt.gizmos.components.modal/close-modal` events with the modal id or by calling the `launch-modal` or `close-modal` helper functions.

### Example
    (rf/reg-event-fx
      ::close-my-modal
      (fn [_ _]
        (println "Modal closed")
        {}))

    (gizmos/modal ::my-modal
                  :title "My Modal"
                  :body [:div "This is a modal"]
                  :footer [:button.button.is-primary {:on-click #(gizmos/close-modal ::my-modal)} "Close"]
                  :on-close [::close-my-modal])
    [:button.button {:on-click #(gizmos/launch-modal ::my-modal)} "Launch Modal"]]

## Message Board
The message board is an area to display popup messages such as alerts.

### Usage
    (gizmos/message-board board-id max-messages-displayed)

The board takes in arguments for the board id and the maximum number of messages displayed. It will also only keep up to 100 messages to prevent a memory leak.

Messages can be added to the board by dispatching `nwbvt.gizmos.components.message-board/info`, `nwbvt.gizmos.components.message-board/warn`, or `nwbvt.gizmos.components.message-board/error`, events along with the board id and notification text. They can also take keyword arguments `keep-for` and `fade` which take the number of seconds to keep the notification and the number of seconds to take to fade it away for temporary messages.

### Example
    (gizmos/message-board ::sample-board :max-messages 3)
      [:div.section
       [:button.button {:on-click #(rf/dispatch [::mb/info ::sample-board "This is an informational message"])} "Add Info Message"]
       [:button.button {:on-click #(rf/dispatch [::mb/warn ::sample-board "This is a warning message"])} "Add Warning Message"]
       [:button.button {:on-click #(rf/dispatch [::mb/error ::sample-board "This is an error message"])} "Add Error Message"]
       [:button.button {:on-click #(rf/dispatch [::mb/info ::sample-board "This is an temporary message" :fade 3 :keep-for 3])} "Add Temp Message"]]
