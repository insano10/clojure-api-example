(ns api.unit.specs.user-test
  (:require [api.specs.user :as ps]
            [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))


(defspec user-is-valid
         20
         (prop/for-all [p (s/gen :api/user)]
                       (s/valid? :api/user p)))

(deftest constructed-user-is-valid
  (is (s/valid? :api/user {:user-id "250fc6a9-074b-4a99-abcb-7e40f235ab83"
                           :name    "jenny"})))
