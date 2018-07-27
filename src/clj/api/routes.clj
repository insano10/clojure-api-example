(ns api.routes
  (:require [clojure.tools.logging :as log]
            [compojure.core :refer [context GET POST DELETE PATCH routes]]
            [compojure.route :as route]
            [integrant.core :as ig]
            [ring.middleware.json :as ring-json]
            [ring.middleware.params :as ring-params]
            [ring.util.http-response :as resp]))

(defn ->routes [{:keys [api-version]}]
  (routes
    (context "/" []
      (GET "/version" [] (resp/ok api-version))

      (GET "/500" [] (throw (Exception. "TEST!!! Here's your 500 hooman.")))

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
      (ring-json/wrap-json-body {:keywords?    true})))
