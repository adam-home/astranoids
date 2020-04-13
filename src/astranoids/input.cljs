(ns astranoids.input
  (:require [astranoids.globals :as globals :refer [scene player]]
            [astranoids.config :as cfg]
            [astranoids.player :as player]
            [astranoids.bullet :as bullet]))

(def KEY_LEFT 37)
(def KEY_RIGHT 39)
(def KEY_UP 38)
(def KEY_L_CTRL 17)
(def KEY_1 49)
(def KEY_PLUS 61)

(def keys-down #{})

(defn process-keys
  []
  (when (contains? keys-down KEY_LEFT)
    (set! player (player/rotate-left player)))

  (when (contains? keys-down KEY_RIGHT)
    (set! player (player/rotate-right player)))

  (when (contains? keys-down KEY_UP)
    (set! player (player/thrust player)))

  (when (contains? keys-down KEY_L_CTRL)
    (set! keys-down (disj keys-down KEY_L_CTRL))
    (if (> cfg/max-bullets (count (filter #(= :player (:object-type (:owner %))) (:bullets scene))))
      (globals/add-object :bullets (bullet/make-bullet player)))))
