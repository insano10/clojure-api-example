(ns api.db.core
  (:gen-class)
  (:require [api.db.queries :as queries]
            [integrant.core :as ig]
            [clojure.java.jdbc :as j]
            [clojure.tools.logging :as log]
            [conman.core :as conman]
            [migratus.core :as migratus])
  (:import [com.opentable.db.postgres.embedded EmbeddedPostgres]))


(def migration-config {:store                :database
                       :migration-dir        "dbmigrations/"
                       :init-in-transaction? true
                       :migration-table-name "migrations"})

(defn- construct-db-url [host-name port database-name username password]
  (format "jdbc:postgresql://%s:%s/%s?user=%s&password=%s" host-name port database-name username password))

(defn- run-migrations [{:keys [host-name port database-name username password]}]
  (try
    (migratus/migrate (assoc migration-config :db (construct-db-url host-name port database-name username password)))
    (catch Exception ex
      (log/error ex "could not run db migrations"))))

(defn- ->conn [{:keys [host-name port database-name username password max-pool-size]
                :or   {max-pool-size 1}}]
  (try
    (conman/connect! {:jdbc-url      (construct-db-url host-name port database-name username password)
                      :max-pool-size max-pool-size})
    (catch Exception ex
      (log/error ex "could not connect to database"))))


(defn insert-user [db-record db]
  (j/with-db-transaction [tx db]
                         (queries/insert-user tx db-record true)))


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
    (log/debug "initialising db")
    (run-migrations opts)
    (->conn opts)
    )

  (defmethod ig/halt-key! ::db [_ conn]
    (conman/disconnect! conn))

