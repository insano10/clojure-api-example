(defproject api-example "0.1.0-SNAPSHOT"

  :description "Example REST API to learn some Clojure"

  :dependencies [
                 [integrant "0.6.3"]
                 [org.clojure/clojure "1.9.0"]
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
