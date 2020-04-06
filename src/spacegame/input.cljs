(ns spacegame.input
  (:require [spacegame.globals :as globals :refer [player bullets]]
            [spacegame.player :as player]
            [spacegame.bullet :as bullet]))

(def KEY_LEFT 37)
(def KEY_RIGHT 39)
(def KEY_UP 38)
(def KEY_L_CTRL 17)
(def KEY_1 49)

(def keys-down #{})

(defn process-keys
  []
  (when (contains? keys-down KEY_LEFT)
    (set! player (player/rotate-left player)))

  (when (contains? keys-down KEY_RIGHT)
    (set! player (player/rotate-right player)))

  (when (contains? keys-down KEY_UP)
    (set! player (player/thrust player)))

  (and (contains? keys-down KEY_L_CTRL)
       (> 5 (count bullets))
       (set! bullets (conj bullets (bullet/make-bullet player)))))

