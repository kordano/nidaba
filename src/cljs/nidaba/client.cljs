(ns nidaba.client
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.browser.repl]
            [cljs.core.async :refer [put! chan <! timeout]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
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
       (mapv
        (fn [x]
          (update-in
           x
           [:date]
           (fn [date] (.toDateString date)))))
       (mapv
        (fn [x]
          (update-in
           x
           [:client-id]
           (fn [id] (-> app :clients id :name)))))
       (mapv
        (fn [x]
          (update-in
           x
           [:payed]
           (fn [x] (if x "yes" "no")))))))


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

(defn appointment-modal [app owner]
  "create modal dialog with inputs for date, id, hours and price"
  (dom/div
   #js {:className "modal fade"
        :id "add-appointment-modal"
        :tabindex "-1"
        :role "dialog"
        :aria-labelledby "add-appointment-modal-label"
        :aria-hidden "true"}

   (dom/div
    #js {:className "modal-dialog"}

    (dom/div
     #js {:className "modal-content"}

     (dom/div
      #js {:className "modal-header"}

      (dom/button
       #js {:type "button"
            :className "close"
            :data-dismiss "modal"
            :aria-hidden "true"}
       "\u00D7")

      (dom/h4
       #js {:className "modal-title"
            :id "add-appointment-modal-label"}
       "Add new Appointment"))

     (dom/div
      #js {:className "modal-body"}

      (dom/form
       #js {:role "form"
            :id "input-form"}

       (dom/div
        #js {:className "form-group"}

        (dom/label
         #js {:for "appointment-input-date"}
         "Date")
        (dom/input
         #js {:type "date"
              :className "form-control"
              :id "appointment-input-date"
              :ref "appointment-input-date"
              :placeholder "YYYY-MM-DD"})

        (dom/label
         #js {:for "appointment-input-hours"}
         "Hours")
        (dom/input
         #js {:className "form-control"
              :type "number"
              :id "appointment-input-hours"
              :ref "appointment-input-hours"})

        (dom/label
         #js {:for "appointment-input-id"}
         "ID")
        (dom/select
         #js {:className "form-control"
              :id "appointment-input-id"
              :ref "appointment-input-id"}
         (doall
          (map #(dom/option #js {:value (name %)} (-> app :clients % :name)) (keys (:clients app)))))


        (dom/label
         #js {:for "appointment-input-price"}
         "Price")
        (dom/input
         #js {:type "number"
              :className "form-control"
              :id "appointment-input-price"
              :ref "appointment-input-price"}))))

     (dom/div
      #js {:className "modal-footer"}

      (dom/button
       #js {:type "button"
            :className "btn btn-default"
            :data-dismiss "modal"}
       "Close")
      (dom/button
       #js {:type "button"
            :className "btn btn-primary"
            :data-dismiss "modal"
            :onClick #(add-appointment app owner)}
       "Energize"))))))



(defn appointment-view [appointment owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/tr nil
             (mapv
              #(dom/td nil (get appointment %))
              [:date :price :client-id :hours :payed])))))


(defn appointments-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:addition (chan)})

    om/IWillMount
    (will-mount [_]
      (let [addition (om/get-state owner :addition)]
        (go
          (loop []
            (let [appointment (<! addition)]
              (.log js/console (str (vals appointment)))
              (om/transact! app :appointment
                            (fn [xs] (vec (sort-by :date > (conj xs appointment)))))
              (recur))))))

    om/IRenderState
    (render-state [this {:keys [addition] :as state}]
      (dom/div
       nil

       ;; --- appointment container ---
       (dom/h1
        #js {:className "page-header"} "Appointments")

       (dom/button
        #js {:className "btn btn-primary"
             :data-toggle "modal"
             :data-target "#add-appointment-modal"}
        "Add Appointment")

       ;; --- modal dialog
       (appointment-modal app owner)

       (dom/div
        #js {:className "table-responsive"}

        (dom/table
         #js {:className "table table-striped"}

         (dom/thead
          nil
          (dom/tr
           nil
           (dom/th nil "Date")
           (dom/th nil "Price")
           (dom/th nil "Name")
           (dom/th nil "Hours")
           (dom/th nil "payed?")))

         (apply dom/tbody nil
                (om/build-all appointment-view (appointment app)
                              {:init-state {:addition addition}}))))))))


(om/root
 appointments-view
 app-state
 {:target (. js/document (getElementById "appointments"))})
