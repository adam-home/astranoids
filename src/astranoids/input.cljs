(ns astranoids.input
  (:require [astranoids.globals :as globals :refer [player]]
            [astranoids.config :as cfg]
            [astranoids.player :as player]
            [astranoids.bullet :as bullet]
            [astranoids.sound :as sound]))

(def KEY_LEFT 37)
(def KEY_RIGHT 39)
(def KEY_UP 38)
(def KEY_L_CTRL 17)
(def KEY_L_SHIFT 16)
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

  (when (or (contains? keys-down KEY_L_CTRL)
            (contains? keys-down KEY_L_SHIFT))
    (set! keys-down (disj keys-down KEY_L_CTRL))
    (set! keys-down (disj keys-down KEY_L_SHIFT))
    (when (> cfg/max-bullets (count (bullet/get-bullets-owned-by-type :player)))
      (globals/add-object :bullets (bullet/make-bullet player))
      (sound/play :laser))))
