(defproject link/link-async "0.1.0-SNAPSHOT"
  :description "a core.async integration for link tcp"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [link "0.12.5"]
                 [org.clojure/core.async "1.1.587"]
                 [org.clojure/tools.logging "1.1.0"]]
  :profiles {:dev {:dependencies [[log4j/log4j "1.2.17"]]}
             :example {:source-paths ["examples"]}}
  :repl-options {:init-ns link-async.core}
  :deploy-repositories {"releases" :clojars})
