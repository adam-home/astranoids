(ns spacegame.levels
  (:require [spacegame.globals :as globals :refer [level scene]]
            [spacegame.input :as input]
            [spacegame.asteroid :as asteroid]
            [spacegame.star :as star]
            [spacegame.player :as player]))

;; Level 0 is attract mode
(def levels
  [
   {:init (fn []
            (set! scene {:name "A S T R A N O I D S\n\nBY ADAM AND PETER T\n\nKEYS LEFT RIGHT UP CTRL"
                         :asteroids (asteroid/make-asteroids 6)}))
    :complete (fn [] (contains? input/keys-down input/KEY_1))}

   {:init (fn []
            (set! scene {:name "EASY STARTER"
                         :asteroids (asteroid/make-asteroids 1)})
            (set! globals/player (player/make-player)))
    :complete (fn [] (= 0 (count (:asteroids globals/scene))))}

   {:init (fn []
            (set! scene {:name "RAMPING UP"
                         :asteroids (asteroid/make-asteroids 4)})
            (set! globals/player (player/reset-player globals/player)))
    :complete (fn [] (= 0 (count (:asteroids globals/scene))))}

   {:init (fn []
            (set! scene {:name "WISH UPON A STAR"
                         :asteroids (asteroid/make-asteroids 4)
                         :stars (list (star/make-star 400 250))})
            (set! globals/player (player/reset-player globals/player)))
    :complete (fn [] (= 0 (count (:asteroids globals/scene))))}

   {:init (fn []
            (set! scene {:name "MINOR CHAOS"
                         :asteroids (asteroid/make-asteroids 4)
                         :stars (list (star/make-star 400 250)
                                      (star/make-star 1000 350))})
            (set! globals/player (player/reset-player globals/player)))
    :complete (fn [] (= 0 (count (:asteroids globals/scene))))}
   ])

(defn level-init
  []
  (set! globals/new-level-timer 100)
  ((:init (get levels (Math/min level (dec (count levels)))))))

(defn level-complete
  []
  ((:complete (get levels (Math/min level (dec (count levels)))))))
