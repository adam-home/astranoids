(ns spacegame.input
  (:require [spacegame.globals :as globals :refer [player bullets]]
            [spacegame.player :as player]
            [spacegame.bullet :as bullet]))

(defn process-keys
  []
  (when (contains? globals/keys-down globals/KEY_LEFT)
    (set! player (player/rotate-left player)))

  (when (contains? globals/keys-down globals/KEY_RIGHT)
    (set! player (player/rotate-right player)))

  (when (contains? globals/keys-down globals/KEY_UP)
    (set! player (player/thrust player)))

  (and (contains? globals/keys-down globals/KEY_L_CTRL)
       (> 5 (count bullets))
       (set! bullets (conj bullets (bullet/make-bullet player)))))
