(defproject nidaba "0.1.0"
  :description "Simple Content Management System"

  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/cljs" "src/clj"]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [mysql/mysql-connector-java "5.1.29"]
                 [com.ashafa/clutch "0.4.0-RC1"]
                 [ring "1.2.1"]
                 [enlive "1.1.5"]
                 [http-kit "2.1.17"]
                 [compojure "1.1.6"]
                 [om "0.5.0"]
                 [com.facebook/react "0.9.0"]
                 [prismatic/dommy "0.1.2"]
                 [hiccups "0.3.0"]
                 [sablono "0.2.6"]
                 [garden "1.1.5"]]

  :plugins [[lein-cljsbuild "1.0.1"]
            [com.cemerick/austin "0.1.3"]
            [lein-ancient "0.5.4"]]

  :repl-options {:init-ns nidaba.core}

  :main ^:skip-aot nidaba.core

  :uberjar-name "nidaba-standalone.jar"

  ;;:hooks [leiningen.cljsbuild]

  :profiles {:uberjar {:aot :all}}

  :cljsbuild
  {:builds
   [{:source-paths ["src/cljs"]
     :compiler
     {:output-to "resources/public/js/main.js"
      :optimizations :simple}}]})
