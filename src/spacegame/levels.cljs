(ns spacegame.levels
  (:require [spacegame.globals :as globals :refer [level]]
            [spacegame.input :as input]
            [spacegame.asteroid :as asteroid]
            [spacegame.star :as star]
            [spacegame.player :as player]))

;; level 0 is attract mode
(defn level-init-0
  []
  (set! globals/asteroids (asteroid/make-asteroids 4)))

(defn level-init-1
  []
  (set! globals/new-level-timer 100)
  (set! globals/asteroids (asteroid/make-asteroids 1))
  (set! globals/bullets #{})
  (set! globals/particles [])
  (set! globals/stars (into #{} (star/make-star 100 100)))
  (set! globals/player (player/make-player)))

(defn level-init-2
  []
  (set! globals/new-level-timer 100)
  (set! globals/asteroids (asteroid/make-asteroids 2))
  (set! globals/bullets #{})
  (set! globals/particles [])
  (set! globals/player (player/reset-player globals/player)))

(defn level-complete-0
  []
  (contains? input/keys-down input/KEY_1))

(defn level-complete-1
  []
  (empty? globals/asteroids))

(defn level-complete-2
  []
  (empty? globals/asteroids))

(defn level-init
  []
  (case level
    0 (level-init-0)
    1 (level-init-1)
    2 (level-init-2)
    (level-init-2)))

(defn level-complete
  []
  (case level
    0 (level-complete-0)
    1 (level-complete-1)
    2 (level-complete-2)
    (level-complete-2)))

