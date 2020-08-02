(ns markdownify.main
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["showdown" :as showdown]))

(defonce markdown (r/atom ""))
(defonce showdown-converter (showdown/Converter.))

(defn md->html [md]
  (.makeHtml showdown-converter md))

(defn app []
  [:h1 "Markdownify"]
  [:div
    {:style {:display :flex}}
    [:div
      {:style {:flex "1"}}
      [:h2 "Markdown"]
      [:textarea
        {:on-change #(reset! markdown (-> % .-target .-value))
         :value @markdown
         :style {:resize "none"
                 :height "200px"
                 :width "100%"}}]]
    
    [:div
      {:style {:flex "1"
               :padding-left "2em"}}
      [:h2 "HTML Preview"]
      [:div 
       {:style {:height "200px"}
        :dangerouslySetInnerHTML {:__html (md->html @markdown)}}]
      [:div (md->html @markdown)]]])
















(defn mount! []
  (rdom/render [app]
                  (.getElementById js/document "app")))

(defn main! []
  (mount!))

(defn reload! []
  (mount!)) 
