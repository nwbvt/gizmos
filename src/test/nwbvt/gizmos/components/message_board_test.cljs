(ns nwbvt.gizmos.components.message-board-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [nwbvt.gizmos.components.message-board :as mb]))

(deftest test-message-board
  (testing "Adding messages"
    (let [init-db {}
          post-db (mb/-add-message {} ::board "Test message" :info)
          messages (get-in post-db [::mb/messages ::board])
          new-message (first messages)]
      (is (= :info (:type new-message)))
      (is (= "Test message" (:text new-message))))
    (let [db (-> {}
                 (mb/-add-message ::board "Test message" :info)
                 (mb/-add-message ::board "Another message" :info)
                 (mb/-add-message ::board2 "Another board's message" :info)
                 (mb/-add-message ::board "Test Error" :error))
          messages (get-in db [::mb/messages ::board])]
      (is (= ["Test message" "Another message" "Test Error"]
             (map :text messages)))
      (is (= [:info :info :error]
             (map :type messages))))))
