(ns api.db.extend
  (:require [cheshire.core :as json]
            [clojure.java.jdbc :as sql]
            [cheshire.parse :as json-parse])
  (:import [clojure.lang IPersistentMap IPersistentVector]
           org.postgresql.util.PGobject))

(defn- to-pg-jsonb-value [str-val]
  (doto
    (PGobject.)
    (.setType "jsonb")
    (.setValue str-val)))

(extend-protocol sql/ISQLValue
  IPersistentMap
  (sql-value
    [value]
    ;; FIXME this could mess up bigdecimals
    (to-pg-jsonb-value (json/generate-string value)))
  IPersistentVector
  (sql-value
    [value]
    ;; FIXME this could mess up bigdecimals
    (to-pg-jsonb-value (json/generate-string value))))

(extend-protocol sql/IResultSetReadColumn
  PGobject
  (result-set-read-column [pgobj _metadata _index]
    (let [value (.getValue pgobj)]
      (if (= (.getType pgobj) "jsonb")
        (binding [json-parse/*use-bigdecimals?* true]
          (json/parse-string value true))
        value))))