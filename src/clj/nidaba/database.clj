(ns nidaba.database
  (:require [clojure.java.jdbc :as sql]))

(def db {:subprotocol "mysql"
         :subname "//localhost:3306/nidaba"
         :user "lisp-god"
         :password "123456"})

(defn create-a-table []
  (sql/db-do-commands
    db
    (sql/create-table-ddl
     :enterprise
     [:id :integer "PRIMARY KEY" "AUTO_INCREMENT"]
     [:series :text]
     [:captain :text]
     [:first_officer :text] ;; no dashes with sql columns ...
     )))

(defn insert-some-data [{series :series captain :captain first-officer :first_officer}]
  (sql/insert!
   db
   :enterprise
   [:series :captain :first_officer]
   [series captain first-officer]))

#_(create-a-table)
(def test-data [{:series "tng" :captain "picard" :first_officer "riker"}
                {:series "original" :captain "kirk" :first_officer "spock"}])

#_(doall (map insert-some-data test-data))

(defn add-child [name phone-parents phone-teacher hourly-rate address-id]
  (sql/insert-record
   db
   :client
   {:name name :hourly-rate hourly-rate :type "'child'"}
   ))

(defn add-appointment [date subject client-id cost hours]
  (sql/insert!
   db
   :appointment
   [:date :subject :client :cost :hours :paid]
   [date subject client-id cost hours 0]))

(defn kill-appointment [id]
  (sql/delete! db :appointment ["id = ?" id]))

(defn create-tables []
 (apply sql/db-do-commands
    db
    (map sql/create-table-ddl
         [ [:appointment
            [:id :integer "PRIMARY KEY" "AUTO_INCREMENT"]
            [:date :date]
            [:subject :text]
            [:client :integer]
            [:cost "decimal(5,2)"]
            [:hours "decimal(4,2)"]
            [:paid :boolean]]
           [:client
            [:id :integer "PRIMARY KEY" "AUTO_INCREMENT"]
            [:name :text]
            [:hourly-rate "decimal(5,2)"]
            [:type "enum('child', 'group', 'institution')"]]
           [:child
            [:id :integer "PRIMARY KEY" "ADD CONSTRAINT fk_child (id) references client (id)"]
            [:phone_parents :integer]
            [:phone_teacher :integer]
            [:billing_address :integer "ADD CONSTRAINT fk_addr (billing_address) references billing_address (id)"]]
           [:group
            [:id :integer "PRIMARY KEY" "ADD CONSTRAINT fk_group (id) references client (id)"]
            [:members :text]
            [:institution :integer]]
           [:institution
            [:id :integer "PRIMARY KEY" "ADD CONSTRAINT fk_institution (id) references client (id)"]
            [:billing_address :integer "ADD CONSTRAINT fk_addr (billing_address) references billing_address (id)"]]
           [:billing_address
            [:id :integer "PRIMARY KEY" "AUTO_INCREMENT"]
            [:title :text]
            [:first_name :text]
            [:last_name :text]
            [:city :text]
            [:street :text]]
           [:phone_number
            [:client :integer "ADD CONSTRAINT fk_client (client) references client (id)"]
            [:subject :text]
            [:number :integer]]
           [:expenses
            [:date "date"]
            [:category :text]
            [:cost "decimal(8,2)"]]])))
