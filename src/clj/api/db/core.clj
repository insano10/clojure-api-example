(ns api.db.core
  (:gen-class)
  (:require [integrant.core :as ig]
            [clojure.tools.logging :as log]
            [conman.core :as conman])
  (:import [com.opentable.db.postgres.embedded EmbeddedPostgres]))

; todo: create a table for api on init

(defn- construct-db-url [host-name port database-name username password]
  (format "jdbc:postgresql://%s:%s/%s?user=%s&password=%s" host-name port database-name username password))

(defn- ->conn [{:keys [host-name port database-name username password max-pool-size]
                :or   {max-pool-size 1}}]
  (try
    (conman/connect! {:jdbc-url      (construct-db-url host-name port database-name username password)
                      :max-pool-size max-pool-size})
    (catch Exception ex
      (log/error ex "could not connect to database"))))




(defmethod ig/init-key ::db-server [_ {:keys [port]}]
  (-> (EmbeddedPostgres/builder)
      (.setPort port)
      (.start)
      ))

(defmethod ig/init-key ::db-server-shutdown [_ _]
  ())

(defmethod ig/halt-key! ::db-server-shutdown [_ db-server]
  (.close db-server))

(defmethod ig/init-key ::db [_ opts]
  (->conn opts))

(defmethod ig/halt-key! ::db [_ conn]
  (conman/disconnect! conn))

