(ns api.db.queries
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "queries.sql")

(defn insert-user
  [conn {:keys [user-id] :as data} log?]
  (let [stored-id (sql-insert-user! conn {:id user-id :data data})]
    (if log?
      (log/info (str "Inserted user with id " stored-id)))))