(ns nidaba.style
  (:require [garden.core :refer [css]]
            [garden.units :refer [px pt]]
            [garden.color :refer [hsl rgb lighten darken]]))

(def dark-palette {:background (hsl 0 0 18)
                   :text (hsl 87 50 92)})

(def solarized
  {:base03 "#002b36"
   :base02 "#073642"
   :base01 "#586e75"
   :base00 "#657b83"
   :base0 "#839496"
   :base1 "#93a1a1"
   :base2 "#eee8d5"
   :base3 "#fdf6e3"
   :yellow "#b58900"
   :orange "#cb4b16"
   :red "#dc322f"
   :magenta "#d33682"
   :violet "#6c71c4"
   :blue "#268bd2"
   :cyan "#2aa198"
   :green "#859900"})


(def full "100%")
(def half "50%")

(defn body [p]
  [:body
    {:background (:base3 p)
     :color (:base01 p)
     :margin 0
     :padding 0
     :border 0
     :font-family "Open Sans"
     :font-size (pt 12)}])

(defn container [p]
  (list
   [:.container
    {:margin {:top "5%"
              :right "15%"
              :left "15%"
              :bottom "5%"}}]
   [:.container-header
    {:font-size (pt 18)
     :font-style :italic}
    [:a
     {:color (:violet p)}]]
   [:.container-list
    [:table
     {:width full
      :border-collapse :collapse
      :margin-top (px 10)
      :text-align :center}
     [:td
      {:border {:style :solid
               :width (px 1)}}]]]
   [:#overlay
    {:visibility :hidden
     :position :absolute
     :width full
     :height full
     :text-align :center
     :z-index 1000}
    [:div
     {:background-color (:base2 p)
      :width (px 300)
      :margin-top "5%"
      :margin-left :auto
      :margin-right :auto
      :border {:width (px 1)
               :style :solid
               :color (:base02 p)}
      :text-align :center}
     [:a
      {:width full
       :margin 0
       :padding 0}]
     [:.overlay-nav
      {:border :none
       :margin-left :auto
       :margin-right :auto
       :margin-bottom (px 5)}]]]))


(defn button [p]
  (list
   [:button
    {:color (:base01 p)
     :background (:base3 p)
     :padding "3px 6px"
     :border {:radius (px 1)
              :width (px 1)
              :style :solid
              :color (:base01 p)}}
    [:&:hover
     {:color (:red p)
      :border-color (:red p)}]]
   [:.header-button
    {:float :right}]))


(defn init []
  (css
   (map #(% solarized) [body container button])))
