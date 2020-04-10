(ns spacegame.core
  (:require [clojure.set :as set]
            [spacegame.globals :as globals :refer [player bullets asteroids particles stars]]
            [spacegame.config :as cfg]
            [spacegame.input :as input]
            [spacegame.drawing :as draw]
            [spacegame.player :as player]
            [spacegame.particle :as part]
            [spacegame.bullet :as bullet]
            [spacegame.asteroid :as asteroid]
            [spacegame.star :as star]
            [spacegame.geometry :as geom]
            [spacegame.collision :as collision]
            [spacegame.levels :as levels]))

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

(defn move-objects
  [objects]
  (set! globals/scene
        (assoc globals/scene
               :asteroids (map globals/move-object (:asteroids globals/scene))
               :bullets (map globals/move-object (:bullets globals/scene)))))
;;  (into #{} (map globals/move-object objects)))

(defn main-loop
  []

  (draw/clear-canvas)

  (draw/draw-string-centre "ASTRANOIDS" :y 4)

  (if (= 0 globals/level)
    (do
      (when (not= 0 globals/games-played)
        (draw/draw-string-centre "GAME OVER"))
        
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
          (set! globals/games-played (inc globals/games-played))
          (set! globals/level 0))
        (input/process-keys))))
  
  (when (> (:lives player) 0)
    (draw/draw-objects (list player)))

  (draw/draw-lives player)
  (draw/draw-score (:score player))

  (draw/draw-scene)
  
  (draw/draw-objects particles)
  ;; (draw/draw-objects bullets)

  (draw/flip)

  (when (> globals/level 0)
    (set! player (player/move-player player)))

  ;; Apply gravity to vulnerable objects
  ;; (doseq [star (filter #(= :star (:object-type %)) globals/scene)]
  ;;   (set! player (first (star/apply-gravity-to-all star (list player))))
  
  ;;   (let [asteroids (filter #(= :asteroid (:object-type %)) globals/scene)]
  ;;     ;; Remove asteroids from scene
  ;;     (set! globals/scene (into #{} (remove #(= :asteroid (:object-type %)) globals/scene)))
  ;;     ;; Add back in, with gravity applied
  ;;     (set! globals/scene (into globals/scene (conj globals/scene (star/apply-gravity-to-all star asteroids))))))

  ;; (set/union globals/scene (star/apply-gravity-to-all star asteroids))))
    ;; (set! asteroids (star/apply-gravity-to-all star asteroids))
  ;; (set! bullets (star/apply-gravity-to-all star bullets))

  ;; (set! player (first (star/apply-gravity-to-all star (list player))))

  (set! particles (part/move-particles particles))
  ;; (set! bullets (bullet/move-bullets bullets))

  (set! globals/scene (move-objects globals/scene))
  
  ;; Do we need to start a new level?
  (when (levels/level-complete)
    (set! globals/level (inc globals/level))
    (levels/level-init)
    (set! globals/new-level-timer cfg/level-message-timeout))

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

  (levels/level-init)
  (.requestAnimationFrame js/window main-loop))
