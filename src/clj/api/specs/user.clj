(ns api.specs.user
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))


(s/def ::non-empty-str
  (s/and string? (complement str/blank?)))

(s/def ::user-id ::non-empty-str)
(s/def ::name ::non-empty-str)

(s/def :api/user (s/keys :req-un [ ::user-id ::name ]
                         :opt []))