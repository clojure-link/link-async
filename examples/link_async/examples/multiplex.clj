(ns link-async.examples.multiplex
  (:gen-class)
  (:require [link.core :as link]
            [link.tcp :as tcp]
            [link.codec :as codec]
            [link-async.core :as linka]
            [link-async.multiplex :as link-multiplex]

            [clojure.core.async :as async]))

(def line-based-codec
  (codec/netty-codec
   (codec/frame
    ;; transaction id
    (codec/uint16)
    (codec/string :encoding "utf8" :delimiter "\r\n"))))

(def server-handler
  (link/create-handler
   (link/on-message [ch msg]
                    (future
                      (Thread/sleep (rand-int 1000))
                      (link/send! ch msg)))))

(defn -main [& args]
  ;; start server
  (tcp/tcp-server 8715 [line-based-codec server-handler])
  (let [;; client handler
        handler (linka/handler-factory (link-multiplex/multiplex-purgatory first))
        ;; client bootstrap
        client-factory (tcp/tcp-client-factory [line-based-codec handler])
        ;; actual client
        the-client (tcp/tcp-client client-factory "127.0.0.1" 8715 :lazy-connect false)
        ;; send the request and get the chan
        the-chan (linka/send-async! the-client [0 "Link async example!"])
        the-chan2 (linka/send-async! the-client [1 "Second frame"])]
    ;; get response from the chan
    (async/go
      (println (first (async/alts! [the-chan the-chan2])))
      (println (first (async/alts! [the-chan the-chan2]))))
    (Thread/sleep 5000)))
