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

(defn -main [& args]
  (let [handler (linka/handler-factory link-pipeline/pipeline-purgatory)
        client-factory (tcp/tcp-client-factory [line-based-codec handler])
        the-client (tcp/tcp-client client-factory "127.0.0.1" 8715 :lazy-connect false)

        the-chan (linka/send-async! the-client ["Link async example!\r\n"])]
    (async/go
      (println (async/<! the-chan)))
    (Thread/sleep 5000)))
