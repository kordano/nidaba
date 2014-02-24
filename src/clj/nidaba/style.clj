(ns nidaba.style
  (:require [garden.core :refer [css]]
            [garden.units :refer [px pt em]]
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
     {:color (:blue p)}]]
   [:.container-list
    [:table
     {:width full
      :border-collapse :collapse
      :margin-top (px 10)
      :text-align :center}
     [:td
      {:border {:style :solid
               :width (px 1)}}]]]))

(defn option [p]
  (list
   [:select
    {:background (:base2 p)
     :color (:base01 p)
     :border-color (:base01 p)}]))

(defn overlay [p]
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
               :color (:base01 p)}
      :text-align :center}
     [:input
      {:background (:base3 p)
       :width (px 200)
       :color (:base01 p)
       :border {:style :solid
                :width (px 1)
                :color (:base01 p)}
       :text-align :center
       :font-size (pt 12)}]
     [:.overlay-header
      {:font-size (pt 14)
       :font-style :italic
       :color (:blue p)}]

     [:.overlay-input
      {:margin-top (px 5)
       :padding 0}]

     [:.overlay-nav
      {:border :none
       :margin-left :auto
       :margin-right :auto
       :margin-bottom (px 5)}]]])


(defn menu [p]
  (list
   [:.menubar-item
    {:display :inline-block
     :margin-top (px 5)
     :position :relative}]

   [:.menubar-item-target
    {:color (:base01 p)
     :background (:base3 p)
     :display :block
     :width (px 200)
     :text-decoration :none
     :border {:style :solid
              :color (:base01 p)
              :width (px 1)}
     :cursor :pointer}
    [:&:hover
     {:background (:blue p)
      :color (:base3 p)}]]

   [:.menu
    {:display :none
     :position :absolute
     :padding 0
     :margin 0
     :top full
     :z-index 1500
     :border {:style :solid
              :color (:base01 p)
              :width (px 1)}
     :border-top 0
     :background (:base3 p)
     :list-style :none
     :width (px 200)}]
   [:.is-selected :.menu-item {:display :block}]

   [:.menu-item-target
    {:color (:base01 p)
     :display :block
     :text-decoration :none}
    [:&:hover
     {:background (:blue p)
      :color (:base3 p)}]]))


(defn button [p]
  (list
   [:button
    {:color (:base01 p)
     :background (:base3 p)
     :padding "4px 8px"
     :border {:radius (px 4)
              :width (px 1)
              :style :solid
              :color (:base01 p)}}
    [:&:hover
     {:color (:red p)
      :border-color (:red p)
      :cursor :pointer}]]
   [:.header-button
    {:float :right}]))


(defn init []
  (css
   (map
    #(% solarized)
    [body container button overlay option menu])))


#_(css [:.menu
    {:display :none
     :position :absolute
     :top full
     :background "#fff"
     :list-style :none
     :width (em 15)
     :padding "10px 0"}
    [".is-selected &" {:display :block}]])
