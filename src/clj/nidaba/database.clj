(ns nidaba.database
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "mysql"
         :subname "//localhost:3306/nitro"
         :user "nitrouser"
         :password "nitro123"})

(defn create-oligo-table []
  (sql/db-do-commands
    db
    (sql/create-table-ddl
     :oligo
     [:id :integer "PRIMARY KEY" "AUTO_INCREMENT"]
     [:oligo :text]
     [:sequence :text]
     [:priming_in :text]
     [:used_for :text]
     [:stored_by :text]
     [:date :date])))

(defn insert-oligo-data [{oligo "oligo" sequence "sequence" priming_in "priming_in" used_for "used_for" stored_by "stored_by" date "date"}]
  (sql/insert!
   db
   :oligo
   [:oligo :sequence :priming_in :used_for :stored_by :date]
   [oligo sequence priming_in used_for stored_by date]))
