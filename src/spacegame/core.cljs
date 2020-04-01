(ns spacegame.core
  (:require [spacegame.config :as cfg]
            [spacegame.globals :as globals]
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

(def player (player/make-player (/ (first cfg/default-canvas-size) 2)
                                (/ (second cfg/default-canvas-size) 2)))

(def KEY_LEFT 37)
(def KEY_RIGHT 39)
(def KEY_UP 38)
(def KEY_L_CTRL 17)
(def keys-down #{})

(.addEventListener js/window ;; (get-elem-by-id "game-canvas")
                   "keydown"
                   (fn [evt]
                     (set! keys-down (conj keys-down (.-keyCode evt))))
                   true)

(.addEventListener js/window
                   "keyup"
                   (fn [evt]
                     (set! keys-down (disj keys-down (.-keyCode evt))))
                   true)


(defn main-loop
  []

  (when (contains? keys-down KEY_LEFT)
    (set! player (player/rotate-left player)))

  (when (contains? keys-down KEY_RIGHT)
    (set! player (player/rotate-right player)))

  (when (contains? keys-down KEY_UP)
    (set! player (player/thrust player)))

  (and (contains? keys-down KEY_L_CTRL)
       (> 5 (count globals/bullets))
       (set! globals/bullets (conj globals/bullets (bullet/make-bullet player))))

  (draw/clear-canvas globals/buffer-ctx :colour "black")

  (player/draw-player player)
  (set! player (player/move-player player))
  
  (part/draw-particles globals/particles)
  (set! globals/particles (part/move-particles globals/particles))

  (bullet/draw-bullets globals/bullets)
  (set! globals/bullets (bullet/move-bullets globals/bullets))

  (asteroid/draw-asteroids globals/asteroids)
  (set! globals/asteroids (asteroid/move-asteroids globals/asteroids))

  (let [player-translated (geom/translate-shape (:shape player) (:x player) (:y player))]
    (doseq [asteroid globals/asteroids]
      (let [asteroid-translated (geom/translate-shape (:shape asteroid) (:x asteroid) (:y asteroid))
            box-translated (geom/translate-box (:box asteroid) (:x asteroid) (:y asteroid)) ]
      
        ;; Check to see if any of the asteroids have hit the player
        (when (collision/polygons-intersect? asteroid-translated player-translated)
          (player/explode player)
          (set! player (player/make-player (/ (first cfg/default-canvas-size) 2)
                                           (/ (second cfg/default-canvas-size) 2))))

        ;; Check to see if any of the asteroids have been hit by a bullet
        (doseq [bullet globals/bullets]
          (when (collision/bullet-intersect? asteroid-translated box-translated bullet)
            (asteroid/explode asteroid)
            (asteroid/remove-asteroid asteroid)
            (bullet/remove-bullet bullet)

            (if (> 3 (:generation asteroid))
              (dotimes [_ 2]
                (set! globals/asteroids
                      (conj globals/asteroids
                            (asteroid/make-asteroid (:x asteroid) (:y asteroid)
                                                    (inc (:generation asteroid)))))
            )))))))


  (.drawImage globals/screen-ctx
              (.-canvas globals/buffer-ctx)
              0 0)

  (when (empty? globals/asteroids)
    (set! globals/level (inc globals/level))
    (asteroid/add-asteroids-to-game (+ 3 globals/level)))
  
  (.requestAnimationFrame js/window
                          (fn []
                            (main-loop)

                            ;; Fire button doesn't repeat
                            (set! keys-down (disj keys-down KEY_L_CTRL))

                            )))
  

(let [[game-canvas buffer] (draw/create-canvas "canvas-container"
                                               (first cfg/default-canvas-size)
                                               (second cfg/default-canvas-size)
                                               :id "game-canvas")]
      ;; game-ctx (.getContext game-canvas "2d")
      ;; buffer-ctx (.getContext buffer "2d")]

  (set! globals/screen-ctx (.getContext game-canvas "2d"))
  (set! globals/buffer-ctx (.getContext buffer "2d"))
  
  (.requestAnimationFrame js/window main-loop))
;;(fn [] (main-loop game-ctx buffer-ctx))))
