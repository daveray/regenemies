(defproject regenemies "0.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [seesaw "1.3.0" :exclusions [com.miglayout/miglayout
                                              com.jgoodies/forms
                                              org.swinglabs.swingx/swingx-core
                                              org.swinglabs/swingx]]]
  :dev-dependencies [[midje "1.3.1"]
                     [lein-midje "1.0.8"]
                     [com.stuartsierra/lazytest "1.2.3"]
                     [com.intelie/lazytest "1.0.0-SNAPSHOT"]]
  :repositories {"stuart" "http://stuartsierra.com/maven2"}
  :main regenemies.main)
