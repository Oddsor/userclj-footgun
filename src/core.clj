(ns core
  (:require [ring.adapter.jetty :as jetty]
            [chat :as chat]))

(defn -main [& args]
  (jetty/run-jetty chat/handler {:port 3000
                                 :join? false}))
