(ns markdownify.main
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["showdown" :as showdown]))

(defonce markdown (r/atom ""))
(defonce html (r/atom ""))

(defonce flash-message (r/atom nil))
(defonce flash-timeout (r/atom nil))

(defonce showdown-converter (showdown/Converter.))

(defn flash
  ([text]
   (flash text 3000))
  ([text ms]
   (reset! flash-message text)
   (reset! flash-timeout (js/setTimeout #(reset! flash-message nil) ms))))

(defn md->html [md]
  (.makeHtml showdown-converter md))

(defn html->md [html]
  (.makeMarkdown showdown-converter html)  )

;;https://hackernoon.com/copying-text-to-clipboard-with-javascript-df4d4988697fc
(defn copy-to-clipboard [s]
  (let [el (.createElement js/document "textarea")
        selected (when (pos? (-> js/document .getSelection .-rangeCount))
                   (-> js/document .getSelection (.getRangeAt 0)))]    
    (set! (.-value el) s)
    (.setAttribute el "readonly" "")
    (set! (-> el .-style .-postion) "abxolute")
    (set! (-> el .-style .-left) "-9999px")
    (-> js/document .-body (.appendChild el))
    (.select el)
    (.execCommand js/document "copy")
    (-> js/document .-body (.removeChild el))
    (when selected
      (-> js/document .getSelection .removeAllRanges)
      (-> js/document .getSelection (.addRange selected)))))

(defn app []
[:div {:style {:position :relative}}
   [:div {:style {:position :absolute
                  :margin :auto
                  :left 0
                  :right 0
                  :text-align :center
                  :max-width 200
                  :padding "2em"
                  :background-color "yellow"
                  :z-index 100
                  :border-radius 20
                  :transform (if @flash-message
                               "scaleY(1)"
                               "scaleY(0)")
                  :transition "transform 0.2s ease-out"}} 
    @flash-message]  
   [:h1 "Markdownify"]
   [:div {:style {:display :flex}}
    [:div
     {:style {:flex "1"}}
     [:h2 "Markdown"]
     [:textarea
      {:on-change (fn [e]
                    (reset! markdown (-> e .-target .-value))
                    (reset! html (md->html (-> e .-target .-value))))
       :value @markdown
       :style {:resize "none"
               :height "200px"
               :width "100%"}}]
     [:button
      {:on-click (fn [] 
                   (copy-to-clipboard @markdown)
                   (flash "Markdown copied to clipboard"))
       :style {:background-color :green
               :margin-top "1em"
               :padding "0.7em"
               :color :white
               :border-radius 10}}
      "Copy Markdown"]]

    [:div
     {:style {:flex "1"
              :padding-left "2 em"}}
     [:h2 "HTML"]
     [:textarea
      {:on-change (fn [e]
                    (reset! markdown (-> e .-target .-value))
                    (reset! html (html->md (-> e .-target .-value))))
       :value @html
       :style {:resize "none"
               :height "200px"
               :width "100%"}}]
     [:button
       {:on-click (fn []
                   (copy-to-clipboard @markdown)
                   (flash "HTML copied to clipboard"))
       :style {:background-color :green
               :margin-top "1em"
               :padding "0.7em"
               :color :white
               :border-radius 10}}
      "Copy HTML"]]

    [:div
     {:style {:flex "1"
              :padding-left "2em"}}
     [:h2 "HTML Preview"]
     [:div
      {:style {:height "200px"}
       :dangerouslySetInnerHTML {:__html @html}}]
     [:button
       {:on-click (fn []
                   (copy-to-clipboard @markdown)
                   (flash "HTML copied to clipboard"))
       :style {:background-color :green
               :margin-top "1em"
               :padding "0.7em"
               :color :white
               :border-radius 10}}
      "Copy HTML"]]]])

(defn mount! []
  (rdom/render [app]
    (.getElementById js/document "app")))

(defn main! []
  (mount!))

(defn reload! []
  (mount!)) 
