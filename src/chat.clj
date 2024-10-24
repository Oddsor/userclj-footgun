(ns chat
  (:require [ring.websocket :as ws]))

(defonce listeners (atom {}))

(defn handler [req]
  (if (ws/upgrade-request? req)
    (let [id (random-uuid)]
      {::ws/listener
       {:on-open
        (fn [socket]
          (swap! listeners #(assoc % id socket))
          (ws/send socket "I will echo your messages"))
        :on-message
        (fn [socket message]
          (doseq [[listener socket] @listeners]
            (when-not (= id listener)
              (ws/send socket message))))
        :on-error
        (fn [_socket]
          (swap! listeners #(dissoc % id)))
        :on-close
        (fn [_socket]
          (swap! listeners #(dissoc % id)))}})
    {:status 400
     :body "Not a websocket request"}))
