(ns api.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [api.config :as config]
            [fipp.edn :as fipp]
            [integrant.core :as ig]
            [medley.core :as m]
            [ring.adapter.jetty :refer [run-jetty]]))

(defmethod ig/init-key ::jetty [_ {:keys [app] :as opts}]
  (run-jetty app (dissoc opts :app)))

(defmethod ig/halt-key! ::jetty [_ server]
  (.stop server))

(defn ->ig-config
  ([config] (m/filter-keys qualified-keyword? config)))

(defn- startup []
  (let [config (config/read-config)
        ig-config (->ig-config config)]
    (log/info "Starting app with\n"
              (with-out-str (fipp/pprint {:config    config
                                          :ig-config ig-config})))
    (ig/load-namespaces ig-config)
    (ig/init ig-config)))

(defn- shutdown
  [system]
  (fn []
    (ig/halt! system)
    (shutdown-agents)))

(defn -main [& args]
  (let [system (startup)]
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. (shutdown system)))))
