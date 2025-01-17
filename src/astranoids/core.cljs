;;
;; TODO
;;
;; Don't allow asteroids, saucers to start close to the player's position
;;

(ns astranoids.core
  (:require [astranoids.globals :as globals :refer [scene player]]
            [astranoids.config :as cfg]
            [astranoids.input :as input]
            [astranoids.drawing :as draw]
            [astranoids.player :as player]
            [astranoids.star :as star]
            [astranoids.collision :as collision]
            [astranoids.levels :as levels]))

(enable-console-print!)

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
  (filter (comp not nil?) (map globals/move-object (type scene))))

(defn move-objects!
  []
  (set! scene
        (assoc scene
               :asteroids (move-objects-of-type :asteroids)
               :bullets (move-objects-of-type :bullets)
               :saucers (move-objects-of-type :saucers)
               :particles (move-objects-of-type :particles))))

(defn update-objects-of-type
  [type]
  (filter (comp not nil?) (map globals/update-object (type scene))))

(defn update-objects!
  []
  (set! scene
        (assoc scene
               :saucers (update-objects-of-type :saucers))))

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
          saucers (:saucers scene)
          bullets (:bullets scene)]
      (set! scene
            (assoc scene
                   :asteroids (star/apply-gravity-to-all star asteroids)
                   :saucers (star/apply-gravity-to-all star saucers)
                   :bullets (star/apply-gravity-to-all star bullets)))))

  (move-objects!)
  
  ;; Do we need to start a new level?
  (when (levels/level-complete)
    (set! globals/level (inc globals/level))
    (levels/level-init)
    (set! globals/new-level-timer cfg/level-message-timeout))

  (update-objects!)
  
  ;; Level-specific updates
  (levels/level-update)  
  
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
