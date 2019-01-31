(ns link-async.pipeline
  (:require [link-async.core :as la])
  (:import [clojure.lang PersistentQueue]))

(defn empty-queue [] (PersistentQueue/EMPTY))

(deftype PipelinePurgatory [state]

  la/IPurgatory
  (reset-state! [this]
    (reset! state (empty-queue)))

  (get-chan [this _]
    (first @state))

  (start-transaction! [this _ chan]
    (swap! state conj chan))

  (end-transaction! [this _]
    (swap! state pop))

  (beginning? [this _] true)

  (terminate? [this _] true))

(defn pipeline-purgatory []
  (PipelinePurgatory. (atom (empty-queue))))
