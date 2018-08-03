(ns api.routes
  (:require [api.db.core :as db.core]
            [cheshire.core :as json]
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

      (POST "/users" {:keys [body]} (let [user body
                                          user-id (:user-id body)]
                                     (db.core/insert-user user db)
                                     (resp/created (format "/users/%s" user-id)
                                                   (json/generate-string user-id))))
      (route/not-found "page not found"))))

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
