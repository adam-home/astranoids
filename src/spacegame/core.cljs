(ns spacegame.core
  (:require [spacegame.config :as cfg]
            [spacegame.globals :as globals :refer [player bullets asteroids particles]]
            [spacegame.input :as input]
            [spacegame.drawing :as draw]
            [spacegame.player :as player]
            [spacegame.particle :as part]
            [spacegame.bullet :as bullet]
            [spacegame.asteroid :as asteroid]
            [spacegame.geometry :as geom]
            [spacegame.collision :as collision]))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(enable-console-print!)

(println "This text is printed from src/spacegame/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(set! player (player/make-player (/ (first cfg/default-canvas-size) 2)
                                 (/ (second cfg/default-canvas-size) 2)))

(.addEventListener js/window ;; (get-elem-by-id "game-canvas")
                   "keydown"
                   (fn [evt]
                     (set! globals/keys-down (conj globals/keys-down (.-keyCode evt))))
                   true)

(.addEventListener js/window
                   "keyup"
                   (fn [evt]
                     (set! globals/keys-down (disj globals/keys-down (.-keyCode evt))))
                   true)

(defn main-loop
  []

  (collision/check-for-collisions)

  (input/process-keys)

  (draw/clear-canvas)

  (draw/draw-lives player)
  (draw/draw-score (:score player))
  
  (player/draw-player player)
  (part/draw-particles particles)
  (bullet/draw-bullets bullets)
  (asteroid/draw-asteroids asteroids)

  (draw/flip)

  (set! player (player/move-player player))
  (set! particles (part/move-particles particles))
  (set! bullets (bullet/move-bullets bullets))
  (set! asteroids (asteroid/move-asteroids asteroids))

  (when (empty? asteroids)
    (set! globals/level (inc globals/level))
    (asteroid/add-asteroids-to-game (+ 3 globals/level)))
  
  (.requestAnimationFrame js/window
                          (fn []
                            (main-loop)

                            ;; Fire button doesn't repeat
                            (set! globals/keys-down (disj globals/keys-down globals/KEY_L_CTRL))

                            )))

(let [[game-canvas buffer] (draw/create-canvas "canvas-container"
                                               (first cfg/default-canvas-size)
                                               (second cfg/default-canvas-size)
                                               :id "game-canvas")]

  (set! globals/screen-ctx (.getContext game-canvas "2d"))
  (set! globals/buffer-ctx (.getContext buffer "2d"))
  
  (.requestAnimationFrame js/window main-loop))
