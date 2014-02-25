(ns nidaba.core
  (:gen-class :main true)
  (:require [cemerick.austin.repls :refer (browser-connected-repl-js)]
            [net.cgrand.enlive-html :as enlive]
            [compojure.route :refer (resources)]
            [compojure.core :refer (GET defroutes)]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.java.io :as io]
            [org.httpkit.server :refer [with-channel on-close on-receive run-server send!]]
            [nidaba.style :as style]))


(defn destructure-request [{type :type data :data }]
  (case type
    "greeting" {:type "greeting" :data "Hail to the LAMBDA!"}
    "DEFAULT"))


                                        ; websocket server
(defn handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data]
                          (do
                            (println (str "data received: " (str (read-string data))))
                            (send! channel (str (destructure-request (read-string data)))))))))


(defn start-ws-server [port]
  (run-server handler {:port port}))


                                        ; ring server
(enlive/deftemplate page
  (io/resource "public/index.html")
  []
  [:body] (enlive/append
           (enlive/html [:script (browser-connected-repl-js)])))

(defroutes site
  (resources "/")
  (GET "/*" req (page)))

(defn run [port]
  (ring.adapter.jetty/run-jetty #'site {:port port :join? false}))

#_(defonce server
  (run 8081))

(defn -main
  [& args]
  (println "Starting ring server")
  (run 8081)
  (println "Starting websocket server")
  (start-ws-server 9090))


#_(.stop server)
#_(.start server)
#_(start-ws-server 9090)
