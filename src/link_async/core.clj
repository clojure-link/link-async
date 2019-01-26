(ns link-async.core
  (:require [link.core :as link]
            [link.tcp :as tcp]))

(defprotocol IPurgatory
  (get-purgatory [this packet] "find the right channel for packet")
  (terminate? [this packet] "test if the packet is a termination of a transaction"))
