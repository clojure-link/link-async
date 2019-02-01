(ns link-async.examples.hello
  (:gen-class)
  (:require [link.core :as link]
            [link.tcp :as tcp]
            [link.codec :as codec]
            [link-async.core :as linka]
            [link-async.pipeline :as link-pipeline]

            [clojure.core.async :as async]))

(def line-based-codec
  (codec/netty-codec
   (codec/frame
    (codec/string :encoding "utf8" :delimiter "\r\n"))))

(def server-handler
  (link/create-handler
   (link/on-message [ch msg]
                    (link/send! ch msg))))

(defn -main [& args]
  ;; start server
  (tcp/tcp-server 8715 [line-based-codec server-handler])
  (let [;; client handler
        handler (linka/handler-factory link-pipeline/pipeline-purgatory)
        ;; client bootstrap
        client-factory (tcp/tcp-client-factory [line-based-codec handler])
        ;; actual client
        the-client (tcp/tcp-client client-factory "127.0.0.1" 8715 :lazy-connect false)
        ;; send the request and get the chan
        the-chan (linka/send-async! the-client ["Link async example!"])
        the-chan2 (linka/send-async! the-client ["Second frame"])]
    ;; get response from the chan
    (async/go
      (println (async/<! the-chan))
      (println (async/<! the-chan2)))
    (Thread/sleep 5000)))
