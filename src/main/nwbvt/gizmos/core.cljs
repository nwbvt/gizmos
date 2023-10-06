(ns nwbvt.gizmos.core
  (:require [re-frame.core :as rf]
            [nwbvt.gizmos.components.pager]
            [nwbvt.gizmos.components.modal]
            [nwbvt.gizmos.components.message-board]
            [nwbvt.gizmos.components.form]))

(def pager nwbvt.gizmos.components.pager/pager)

(def modal nwbvt.gizmos.components.modal/modal)

(def launch-modal nwbvt.gizmos.components.modal/launch-modal)

(def close-modal nwbvt.gizmos.components.modal/close-modal)

(def message-board nwbvt.gizmos.components.message-board/message-board)

(def info-message nwbvt.gizmos.components.message-board/info-message)

(def warn-message nwbvt.gizmos.components.message-board/warn-message)

(def error-message nwbvt.gizmos.components.message-board/error-message)

(def form nwbvt.gizmos.components.form/form)
