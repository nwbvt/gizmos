(ns nwbvt.gizmos.components.message-board-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [nwbvt.gizmos.components.message-board :as mb]))

(deftest test-message-board
  (testing "Adding messages"
    (let [init-db {}
          post-db (mb/-add-message {} ::board (mb/-new-message "Test message" :info))
          messages (vals (get-in post-db [::mb/messages ::board]))
          new-message (first messages)]
      (is (= :info (:type new-message)))
      (is (= "Test message" (:text new-message))))
    (let [db (-> {}
                 (mb/-add-message ::board (mb/-new-message "Test message" :info))
                 (mb/-add-message ::board (mb/-new-message "Another message" :info))
                 (mb/-add-message ::board2 (mb/-new-message "Another board's message" :info))
                 (mb/-add-message ::board (mb/-new-message "Test Error" :error)))
          messages (vals (get-in db [::mb/messages ::board]))]
      (is (= ["Test message" "Another message" "Test Error"]
             (map :text messages)))
      (is (= [:info :info :error]
             (map :type messages)))))
  (testing "Deleting messages"
    (let [db (-> {}
                 (mb/-add-message ::board (mb/-new-message "Test message" :info))
                 (mb/-add-message ::board (mb/-new-message "Another message" :info))
                 (mb/-add-message ::board2 (mb/-new-message "Another board's message" :info))
                 (mb/-add-message ::board (mb/-new-message "Test Error" :error)))
          pre-messages (vals (get-in db [::mb/messages ::board]))
          message-ids (map :id pre-messages)
          post-db (-> db
                   (mb/-delete-message ::board (first message-ids))
                   (mb/-delete-message ::board2 (second message-ids)))
          post-messages (vals (get-in post-db [::mb/messages ::board]))]
      (is (= ["Another message" "Test Error"]
             (map :text post-messages))))
    (testing "Fade messages"
      (let [db (-> {}
                   (mb/-add-message ::board {:type :info :text "Test message" :id 1})
                   (mb/-add-message ::board {:type :info :text "Another message" :id 2})
                   (mb/-add-message ::board2 {:type :info :text "Another board's message" :id 3})
                   (mb/-add-message ::board {:type :error :text "Test Error" :id 4}))
            post-db (-> db
                        (mb/-fade-message ::board 1 10)
                        (mb/-fade-message ::board 5 10)
                        (mb/-fade-message ::board2 2 10))
            post-messages (vals (get-in post-db [::mb/messages ::board]))]
        (is (= ["Test message" "Another message" "Test Error"]
             (map :text post-messages)))
        (is (= [1 2 4] (map :id post-messages)))
        (is (= [10 nil nil] (map :fade post-messages)))))))
