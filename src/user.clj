(ns user
  (:require [ring.websocket :as ws]
            [chat :as chat]))

(comment
  ;; Send to all consumers!
  (doseq [[id socket] @chat/listeners]
    (try
      (ws/send socket "Hello!")
      (catch Exception e (println e)))))
