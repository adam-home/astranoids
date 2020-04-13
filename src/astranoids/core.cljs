(ns astranoids.core
  (:require [clojure.set :as set]
            [astranoids.globals :as globals :refer [scene player]]
            [astranoids.config :as cfg]
            [astranoids.input :as input]
            [astranoids.drawing :as draw]
            [astranoids.player :as player]
            [astranoids.particle :as part]
            [astranoids.bullet :as bullet]
            [astranoids.asteroid :as asteroid]
            [astranoids.star :as star]
            [astranoids.geometry :as geom]
            [astranoids.collision :as collision]
            [astranoids.levels :as levels]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(.addEventListener js/window
                   "keydown"
                   (fn [evt]
                     (set! input/keys-down (conj input/keys-down (.-keyCode evt))))
                   true)

(.addEventListener js/window
                   "keyup"
                   (fn [evt]
                     (set! input/keys-down (disj input/keys-down (.-keyCode evt))))
                   true)

(defn move-objects-of-type
  [type]
  (filter (comp not nil?)  (map globals/move-object (type scene))))

(defn move-objects
  [objects]
  (set! scene
        (assoc scene
               :asteroids (move-objects-of-type :asteroids)
               :bullets (move-objects-of-type :bullets)
               :particles (move-objects-of-type :particles))))

(defn main-loop
  []

  (draw/clear-canvas)

  (draw/draw-string-centre "ASTRANOIDS" :y 4)

  (if (= 0 globals/level)
    (do
      (when (not= 0 globals/games-played)
        (draw/draw-string-centre "GAME OVER"))
      
      (draw/draw-string-centre "PRESS 1 TO PLAY" :y-offset -40)
      (draw/draw-string-multiline-offset (:name scene) 40))
    (do
      (collision/check-for-collisions)

      (when (> globals/new-level-timer 0)
        (set! globals/new-level-timer (dec globals/new-level-timer))
        (draw/draw-string-centre (str "LEVEL " globals/level) :y-offset -20)
        (when (:name scene)
          (draw/draw-string-multiline-offset (:name scene) 20)))
      
      ;; Show message if all lives lost
      ;; If still alive, process keypresses
      (if (<= (:lives player) 0)
        (do
          (set! globals/games-played (inc globals/games-played))
          (set! globals/level 0))
        (do
          (input/process-keys)

          (when (contains? input/keys-down input/KEY_PLUS)
            (set! input/keys-down (disj input/keys-down input/KEY_PLUS))
            (set! globals/level (inc globals/level))
            (levels/level-init))))))  
  
  (when (> (:lives player) 0)
    (draw/draw-objects (list player)))

  (draw/draw-lives player)
  (draw/draw-score (:score player))

  (draw/draw-scene)
  
  (draw/flip)

  (when (> globals/level 0)
    (set! player (player/move-player player)))

  ;; Apply gravity to vulnerable objects
  (doseq [star (:stars scene)]
    (set! player (first (star/apply-gravity-to-all star (list player))))

    (let [asteroids (:asteroids scene)
          bullets (:bullets scene)
          particles (:particles scene)]
      (set! scene
            (assoc scene
                   :asteroids (star/apply-gravity-to-all star asteroids)
                   :bullets (star/apply-gravity-to-all star bullets)
                   :particles (star/apply-gravity-to-all star particles)))))

  (set! scene (move-objects scene))
  
  ;; Do we need to start a new level?
  (when (levels/level-complete)
    (set! globals/level (inc globals/level))
    (levels/level-init)
    (set! globals/new-level-timer cfg/level-message-timeout))

  (.requestAnimationFrame js/window
                          (fn []
                            (main-loop)
                            )))

(let [[game-canvas buffer] (draw/create-canvas "canvas-container"
                                               (first cfg/default-canvas-size)
                                               (second cfg/default-canvas-size)
                                               :id "game-canvas")]

  (set! globals/screen-ctx (.getContext game-canvas "2d"))
  (set! globals/buffer-ctx (.getContext buffer "2d"))

  (levels/level-init)
  (.requestAnimationFrame js/window main-loop))
