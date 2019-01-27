(ns link-async.core
  (:require [link.core :as link]
            [link.tcp :as tcp]
            [clojure.tools.logging :as logging]
            [clojure.core.async :as async]))

(defprotocol IPurgatory
  (reset-state! [this] "reset initial state of purgatory")
  (get-chan [this packet] "find the right chan for packet")
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
                          (async/go
                            (async/>! the-chan msg))
                          (logging/debug "chan not found for msg" msg)))
       (link/on-error [ch exc]
                      (logging/warn "error on connection" ch exc)
                      (link/close! ch))
       (link/on-inactive [ch]
                         )))))
