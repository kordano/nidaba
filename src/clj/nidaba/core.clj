(ns nidaba.core
  (:gen-class :main true)
  (:require [cemerick.austin.repls :refer (browser-connected-repl-js)]
            [net.cgrand.enlive-html :as enlive]
            [compojure.route :refer (resources)]
            [compojure.core :refer (GET defroutes POST)]
            [clojure.java.io :as io]
            [org.httpkit.server :refer [with-channel on-close on-receive run-server send!]]
            [nidaba.warehouse :as warehouse]))


(defn dispatch-request [{:keys [topic data]}]
  (case topic
    :greeting {:topic :greeting :data "Hail to the LAMBDA!"}
    "DEFAULT"))


(defn handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data]
                          (do
                            (pprint (str "data received: " (str (read-string data))))
                            (send! channel (str (dispatch-request (read-string data)))))))))


(enlive/deftemplate page
  (io/resource "public/index.html")
  []
  [:body] (enlive/append
           (enlive/html [:script (browser-connected-repl-js)])))

(defroutes site
  (resources "/")
  (GET "/nidaba/ws" [] handler) ;; websocket handling
  (GET "/*" req (page)))

(defn run [port]
  (run-server site {:port port :join? false}))

(defn -main
  [& args]
  (println "Checking database...")
  (warehouse/init-db)
  (println "Starting ring server")
  (run 8081))


#_(def server (run 8081))

#_(server)
