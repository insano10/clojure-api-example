(ns api.routes
  (:require [api.db.core :as db.core]
            [api.specs.user :as user]
            [cheshire.core :as json]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [compojure.core :refer [context GET POST DELETE PATCH routes]]
            [compojure.route :as route]
            [integrant.core :as ig]
            [ring.middleware.json :as ring-json]
            [ring.middleware.params :as ring-params]
            [ring.util.http-response :as resp]))

(defn ->routes [{:keys [api-version db]}]
  (routes
    (context "/" []
      (GET "/version" [] (resp/ok api-version))

      (GET "/500" [] (throw (Exception. "TEST!!! Here's your 500 hooman.")))

      (GET "/users/:user-id" [user-id] (when-let [user (db.core/get-user user-id db)]
                                         (resp/ok user)))

      (POST "/users" {:keys [body]} (cond
                                      (not (s/valid? :api/user body))
                                      (resp/bad-request (s/explain-str :api/user body))
                                      :else
                                      (let [user body user-id (:user-id body)]
                                        (db.core/insert-user user db)
                                        (resp/created (format "/users/%s" user-id)
                                                      (json/generate-string user-id)))))

      (route/not-found "resource not found"))))

(defn wrap-exception-logging
  [handler]
  (fn [req]
    (try (handler req)
         (catch Exception ex
           (log/error ex "Internal server error! Request:\n" req)
           (resp/internal-server-error "Internal server error!")))))

(defmethod ig/init-key ::app [_ opts]
  (-> (->routes opts)
      wrap-exception-logging
      (ring-params/wrap-params)
      (ring-json/wrap-json-response)
      (ring-json/wrap-json-body {:keywords? true})))
