(ns nidaba.client
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.browser.repl]
            [cljs.core.async :refer [put! chan <! timeout]]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true])
  (:require-macros [hiccups.core :as hiccups]
                   [cljs.core.async.macros :refer [go]]))

;; fire up repl
#_(do
    (def repl-env (reset! cemerick.austin.repls/browser-repl-env
                         (cemerick.austin/repl-env)))
    (cemerick.austin.repls/cljs-repl repl-env))


(set! clojure.core/*print-fn* (fn [& s] (.log js/console (apply str s))))


(enable-console-print!)


(def app-state
  (atom {:appointment
         [{:date (js/Date. 2014 3 11) :client-id :1000 :price 25 :hours 3 :payed true}
          {:date (js/Date. 2014 2 11) :client-id :1001 :price 32 :hours 3 :payed true}
          {:date (js/Date. 2013 12 6) :client-id :1000 :price 11 :hours 5 :payed false}
          {:date (js/Date. 2014 4 15) :client-id :1000 :price 30 :hours 1 :payed true}
          {:date (js/Date. 2014 3 21) :client-id :1002 :price 25 :hours 2 :payed true}
          {:date (js/Date. 2014 5 3) :client-id :1002 :price 45 :hours 2 :payed false}
          {:date (js/Date. 2014 2 25) :client-id :1003 :price 35 :hours 3 :payed true}]
         :clients
         {:1000 {:name "Picard" :hourly-rate 11}
          :1001 {:name "Riker" :hourly-rate 6}
          :1002 {:name "Data" :hourly-rate 17}
          :1003 {:name "Troi" :hourly-rate 3}
          :1004 {:name "Crusher" :hourly-rate 12}}}))


;; --- state helper ---

(defn clients [app]
  (->> (:clients app)
       vals
       (mapv :name)))


(defn appointment [app]
  (->> (:appointment app)
       (mapv (fn [x] (update-in x [:date] (fn [date] (.toDateString date)))))
       (mapv (fn [x] (update-in x [:client-id] (fn [id] (-> app :clients id :name)))))))


(defn add-appointment [app owner]
  (let [date (js/Date. (.-value (om/get-node owner "appointment-input-date")))
        hours (js/parseFloat (.-value (om/get-node owner "appointment-input-hours")))
        id (keyword (.-value (om/get-node owner "appointment-input-id")))
        price (js/parseFloat (.-value (om/get-node owner "appointment-input-price")))
        entry {:date date :hours hours :client-id id :price price :payed false}]
    (go
      (put! (om/get-state owner :addition) entry)
      (.reset (.getElementById js/document "input-form")))))


;; --- view ---

(defn appointment-alert [app owner]
  (html
   [:div#appointment-alert.alert.alert-success.alert-dismissable.fade.in
    {:aria-hidden "true"}
    "Appointment successfully added"
    [:button.close
     {:type "button"
      :data-dismiss "alert"
      :aria-hidden "true"}
     "\u00D7"]]))


(defn appointment-modal [app owner]
  "create modal dialog with inputs for date, id, hours and price"
   [:div#add-appointment-modal
    {:class "modal fade"
     :tabindex "-1"
     :role "dialog"
     :aria-labelledby "add-appointment-modal-label"
     :aria-hidden "true"}

    [:div.modal-dialog
     [:div.modal-content

      [:div.modal-header
       [:button.close {:type "button" :data-dismiss "modal" :aria-hidden "true"}
        "\u00D7"]
       [:h4#add-appointment-modal-label.modal-title
        "Add new Appointment"]]

      [:div.modal-body
       [:form#input-form {:role "form"}
        [:div.form-group

         [:label {:for "appointment-input-date"} "Date"]
         [:input#appointment-input-date.form-control
           {:type "date"
            :ref "appointment-input-date"
            :placeholder "YYYY-MM-DD"}]

         [:label {:for "appointment-input-hours"} "Hours"]
         [:input#appointment-input-hours.form-control
          {:type "number"
           :ref "appointment-input-hours"}]

         [:label {:for "appointment-input-id"} "ID"]
         [:select#appointment-input-id.form-control {:ref "appointment-input-id"}
          (doall
           (map
            #(vec [:option {:value (name %)} (-> app :clients % :name)])
            (keys (:clients app))))]

         [:label {:for "appointment-input-price"} "Price"]
         [:input#appointment-input-price.form-control
          {:type "number"
           :ref "appointment-input-price"}]]]]

     [:div.modal-footer
      [:button.btn.btn-default
       {:type "button"
        :data-dismiss "modal"}
       "Close"]

      [:button.btn.btn-primary
       {:type "button"
        :data-dismiss "modal"
        :on-click #(add-appointment app owner)}
       "Energize"]]]]])



(defn appointment-view [appointment owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [payed]}]
      (html
       [:tr
        (map
         #(vec [:td (get appointment %)])
         [:date :price :client-id :hours])
        [:td
         [:div.checkbox
          [:input {:type "checkbox"
                   :checked (appointment :payed)
                   :on-click #(put! payed @appointment)}]]]]))))


(defn appointments-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:addition (chan)
       :payed (chan)})

    om/IWillMount
    (will-mount [_]
      (let [addition (om/get-state owner :addition)
            payed (om/get-state owner :payed)]
        (go
          (loop []
            (let [[appointment c] (alts! [addition payed])]
              (condp = c
                addition (om/transact!
                          app
                          :appointment
                          (fn [xs] (vec (sort-by :date > (conj xs appointment)))))
                payed (om/transact!
                       app
                       :appointment
                       (fn [xs] (mapv
                                (fn [x]
                                  (if (= x appointment)
                                    (update-in x [:payed] not)
                                    x))
                                xs))))
              (recur))))))

    om/IRenderState
    (render-state [this {:keys [addition payed] :as state}]
      (html
       [:div

        ;; --- appointment container ---
        [:h1.page-header "Appointments"]

        [:div.row
         [:div.col-md-4
          [:button.btn.btn-primary
           {:data-toggle "modal"
            :data-placement "left"
            :title "Add a new appointment"
            :data-target "#add-appointment-modal"}
           "Add Appointment"]]]

        ;; --- modal dialog
        (appointment-modal app owner)

        [:div.table-responsive
         [:table.table.table-striped
          [:thead
           [:tr
            [:th "Date"]
            [:th "Price"]
            [:th "Name"]
            [:th "Hours"]
            [:th "payed?"]]]
          [:tbody
           (om/build-all appointment-view (appointment app)
                         {:init-state {:addition addition
                                       :payed payed}})]]]]))))


(om/root
 appointments-view
 app-state
 {:target (. js/document (getElementById "appointments"))})
