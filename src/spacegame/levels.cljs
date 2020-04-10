(ns spacegame.levels
  (:require [spacegame.globals :as globals :refer [level]]
            [spacegame.input :as input]
            [spacegame.asteroid :as asteroid]
            [spacegame.star :as star]
            [spacegame.player :as player]))

;; level 0 is attract mode
(defn level-init-0
  []
  (set! globals/scene
        {
         :asteroids (asteroid/make-asteroids 4)
         }))

(defn level-init-1
  []
  (set! globals/new-level-timer 100)
  (set! globals/scene
        {
         :asteroids (asteroid/make-asteroids 4)
         })
  (set! globals/player (player/make-player)))

(defn level-init-2
  []
  (set! globals/new-level-timer 100)
  (set! globals/scene
        {
         :asteroids (asteroid/make-asteroids 4)
         :stars (list (star/make-star 400 250))
         })
  (set! globals/player (player/reset-player globals/player)))

(defn level-init-3
  []
  (set! globals/new-level-timer 100)
  (set! globals/scene
        {
         :asteroids (asteroid/make-asteroids 4)
         :stars (list (star/make-star 400 250) (star/make-star 1000 350))
         })
  (set! globals/player (player/reset-player globals/player)))

(defn level-complete-0
  []
  (contains? input/keys-down input/KEY_1))

(defn level-complete-1
  []
  (= 0 (count (:asteroids globals/scene))))

(defn level-complete-2
  []
  (= 0 (count (:asteroids globals/scene))))

(defn level-complete-3
  []
  (= 0 (count (:asteroids globals/scene))))

(defn level-init
  []
  (case level
    0 (level-init-0)
    1 (level-init-1)
    2 (level-init-2)
    3 (level-init-3)
    (level-init-3)))

(defn level-complete
  []
  (case level
    0 (level-complete-0)
    1 (level-complete-1)
    2 (level-complete-2)
    3 (level-complete-3)
    (level-complete-3)))

  
