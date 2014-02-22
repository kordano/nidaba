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


(def app-state (atom {:appointment
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


(defn overlay []
  (let [el (.getElementById js/document "overlay")]
    (if (= "visible" (.-visibility (.-style el)))
      (set! (.-visibility (.-style el)) "hidden")
      (set! (.-visibility (.-style el)) "visible"))))


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
    om/IRender
    (render [this]
      (dom/div
       nil

       (dom/div
        #js {:className "cotainer-input" :id "overlay"}
        (dom/div
         nil
         (dom/a nil "Substanz ist Form und Inhalt.")
         (dom/div
          #js {:className "overlay-nav"}

          (dom/button
           #js {:className "overlay-button"
                :onClick (fn [] (overlay))}
           "Cancel")

          (dom/button
           #js {:className "overlay-button"
                :onClick (fn [] (overlay))}
           "Ok"))))

       (dom/div
        #js {:className "container"}

        (dom/div
         #js {:className "container-header"}

         (dom/a nil "Appointment List")

         (dom/button
          #js {:className "header-button" :onClick (fn [e] (overlay))}
          "push it"))


        (dom/div
         #js {:className "container-list"}

         (apply dom/table nil

                (dom/tr nil
                        (dom/th nil "Date")
                        (dom/th nil "Price")
                        (dom/th nil "Name")
                        (dom/th nil "Hours")
                        (dom/th nil "payed?"))

                (om/build-all appointment-view (appointment app)))))))))


(om/root
 appointments-view
 app-state
 {:target (. js/document (getElementById "appointments"))})
