(defproject api-example "0.1.0-SNAPSHOT"

  :description "Example REST API to learn some Clojure"

  :dependencies [
                 [aero "1.1.2"]
                 [cheshire "5.8.0"]
                 [compojure "1.6.0"]
                 [fipp "0.6.12"]
                 [integrant "0.6.3"]
                 [medley "1.0.0"]
                 [metosin/ring-http-response "0.9.0"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/test.check "0.10.0-alpha2"]
                 [org.clojure/tools.logging "0.4.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-json "0.4.0"]
                ]

  :main api.core
  :uberjar-name "api-example.jar"

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]

  :test-selectors {:default (complement :integration)
                   :integration :integration}

  :plugins [[lein-eftest "0.4.3"]]

  :profiles {:uberjar {:aot :all}
             :dev {
                   :resource-paths ["test/resources"]
                  }

             :junit   [:dev :test {:eftest {:report         eftest.report.junit/report
                                            :report-to-file "target/test_results/local/junit.xml"}}]
             :junit-integration   [:junit {:eftest {:report-to-file "target/test_results/integration/junit.xml"}}]})
