(ns link-async.multiplex
  (:require [link-async.core :as la]))

(deftype MultiplexPurgatory [txn-id-fn state]

  la/IPurgatory
  (reset-state! [this]
    (reset! state {}))

  (get-chan [this packet]
    (when-let [txn-id (txn-id-fn packet)]
      (@state txn-id)))

  (start-transaction! [this packet chan]
    (swap! state assoc (txn-id-fn packet) chan))

  (end-transaction! [this packet]
    (swap! state dissoc (txn-id-fn packet)))

  (beginning? [this packet]
    (if-let [txn-id (txn-id-fn packet)]
      (not (contains? @state txn-id))
      false))

  (terminate? [this packet]
    (some? (txn-id-fn packet))))

(defn multiplex-purgatory [txn-id-fn]
  (MultiplexPurgatory. txn-id-fn (atom {})))
