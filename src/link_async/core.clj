(ns link-async.core
  (:require [link.core :as link]
            [link.tcp :as tcp]
            [clojure.tools.logging :as logging]
            [clojure.core.async :as async])
  (:import [link.core ClientSocketChannel]))

(defprotocol IPurgatory
  (reset-state! [this] "reset initial state of purgatory")
  (get-chan [this packet] "find the right chan for packet")
  (start-transaction! [this packet chan] "store core.async chan for a transaction")
  (end-transaction! [this packet] "end a client-server transaction")
  (terminate? [this packet] "test if the packet is a termination of a transaction"))

(def channel-attr-purgatory "link.async/purgatory")

(defn handler-factory [purgatory-fn]
  (let [purgatory (purgatory-fn)]
    (fn []
      (link/create-handler
       (link/on-active [ch]
                       (logging/debug "Channel recreated. Reset purgatory.")
                       (reset-state! purgatory)
                       (link/channel-attr-set! ch channel-attr-purgatory purgatory))
       (link/on-message [ch msg]
                        (if-let [the-chan (get-chan purgatory msg)]
                          (do
                            (async/go
                              ;; TODO: ref count for bytebuf
                              (async/>! the-chan msg))
                            (when (terminate? purgatory msg)
                              (end-transaction! purgatory msg)))
                          (logging/debug "chan not found for msg" msg)))
       (link/on-error [ch exc]
                      (logging/warn "error on connection" ch exc)
                      (link/close! ch))
       (link/on-inactive [ch]
                         )))))

(defprotocol IAsyncClientChannel
  (send-async! [this msg] "Send msg to client as get a core.async chan for response"))

(extend-protocol IAsyncClientChannel
  ClientSocketChannel
  (send-async! [this msg]
    (let [ret-chan (async/chan)
          cb (fn [channel-future]
               (when-let [ch (.channel channel-future)]
                 (when-let [purgatory (link/channel-attr-get ch channel-attr-purgatory)]
                   (start-transaction! purgatory msg ret-chan))))]
      (link/send!* this msg cb)
      ret-chan))))
