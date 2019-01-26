(defproject link/link-async "0.1.0-SNAPSHOT"
  :description "a core.async integration for link tcp"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [link "0.12.2"]
                 [org.clojure/core.async "0.4.490"]]
  :repl-options {:init-ns link-async.core})
