(ns nidaba.warehouse
  (:refer-clojure :exclude [assoc! conj! dissoc! ==])
  (:require [clojure.core :as core]
            [com.ashafa.clutch.utils :as utils]
            [com.ashafa.clutch :refer [with-db get-database get-document put-document update-document all-documents]]))


(defn now [] (new java.util.Date))


(def host (or (System/getenv "DB_PORT_5984_TCP_ADDR") "localhost"))


(defn database-url [database]
  (utils/url (utils/url (str "http://" host ":5984")) database))


(defn init-db []
  (get-database (database-url "appointment")))


(defn add-appointment [{:keys [date subject client cost hours] :as appointment}]
  (put-document (database-url "appointment") (assoc appointment :paid false)))


(defn get-all-appointments []
  (with-db (database-url "appointment")
    (let [ids (map #(:id %) (all-documents))]
      (mapv #(dissoc (get-document %) :_rev) ids))))
