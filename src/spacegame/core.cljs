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

(.addEventListener js/window ;; (get-elem-by-id "game-canvas")
                   "keydown"
                   (fn [evt]
                     (set! input/keys-down (conj input/keys-down (.-keyCode evt))))
                   true)

(.addEventListener js/window
                   "keyup"
                   (fn [evt]
                     (set! input/keys-down (disj input/keys-down (.-keyCode evt))))
                   true)

(defn main-loop
  []

  (draw/clear-canvas)

  (draw/draw-string-centre "ASTRANOIDS" :y 4)

  (if (not globals/game-started)
    (do
      (input/process-keys-attract-mode)
      (draw/draw-string-centre "PRESS 1 TO PLAY" :y-offset -40))

    (do
      (collision/check-for-collisions)

      (when (> globals/new-level-timer 0)
        (set! globals/new-level-timer (dec globals/new-level-timer))
        (draw/draw-string-centre (str "LEVEL " globals/level) :y-offset -20))
  
      ;; Show message if all lives lost
      ;; If still alive, process keypresses
      (if (<= (:lives player) 0)
        (do
          (draw/draw-string-centre "GAME OVER")
          (set! globals/game-started false))
        (input/process-keys))))

  (when (> (:lives player) 0)
    (player/draw-player player))

  (draw/draw-lives player)
  (draw/draw-score (:score player))
    
  (part/draw-particles particles)
  (bullet/draw-bullets bullets)

  ;; Always draw asteroids
  (asteroid/draw-asteroids asteroids)

  (draw/flip)

  (when globals/game-started
    (set! player (player/move-player player)))

  (set! particles (part/move-particles particles))
  (set! bullets (bullet/move-bullets bullets))

  ;; Always move asteroids
  (set! asteroids (asteroid/move-asteroids asteroids))

  (when (empty? asteroids)
    (set! globals/level (inc globals/level))
    (set! player (assoc player :shield 250))
    (set! globals/new-level-timer cfg/level-message-timeout)
    (asteroid/add-asteroids-to-game (+ 3 globals/level)))
  
  (.requestAnimationFrame js/window
                          (fn []
                            (main-loop)

                            ;; Fire button doesn't repeat
                            (set! input/keys-down (disj input/keys-down input/KEY_L_CTRL))

                            )))

(let [[game-canvas buffer] (draw/create-canvas "canvas-container"
                                               (first cfg/default-canvas-size)
                                               (second cfg/default-canvas-size)
                                               :id "game-canvas")]

  (set! globals/screen-ctx (.getContext game-canvas "2d"))
  (set! globals/buffer-ctx (.getContext buffer "2d"))
  
  (.requestAnimationFrame js/window main-loop))
