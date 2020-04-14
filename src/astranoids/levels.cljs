(ns astranoids.levels
  (:require [astranoids.globals :as globals :refer [level scene]]
            [astranoids.config :as cfg]
            [astranoids.input :as input]
            [astranoids.asteroid :as asteroid]
            [astranoids.star :as star]
            [astranoids.saucer :as saucer]
            [astranoids.player :as player]))

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
            (set! scene {:name "SHOOTING BACK"
                         :asteroids (asteroid/make-asteroids 2)
                         :saucers (list (saucer/make-saucer 100 100))})
            (set! globals/player (player/make-player)))
    :complete (fn [] (and (= 0 (count (:asteroids globals/scene)))
                          (= 0 (count (:saucers globals/scene)))))
    :update (fn []
              ;; Create a new saucer every so often, as long as the player is alive
              (when (and (> (:lives globals/player) 0)
                         (< (globals/count-objects :saucers) cfg/max-saucers)
                         (< 998 (rand 1000)))
                (globals/add-object :saucers (saucer/make-saucers 1))))
    }

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

   {:init (fn []
            (set! scene {:name "TOTAL CHAOS"
                         :asteroids (asteroid/make-asteroids 4)
                         :stars (list (star/make-star 400 250)
                                      (star/make-star 1000 350))})
            (set! globals/player (player/reset-player globals/player)))
    :complete (fn [] (and (= 0 (count (:asteroids globals/scene)))
                          (= 0 (count (:saucers globals/scene)))))
    :update (fn []
              ;; Create a new saucer every so often, as long as the player is alive
              (when (and (> (:lives globals/player) 0)
                         (< (globals/count-objects :saucers) cfg/max-saucers)
                         (< 998 (rand 1000)))
                (globals/add-object :saucers (saucer/make-saucers 1))))
    }

   ])

(defn level-init
  []
  (set! globals/new-level-timer 100)
  ((:init (get levels (Math/min level (dec (count levels)))))))

(defn level-complete
  []
  ((:complete (get levels (Math/min level (dec (count levels)))))))

(defn level-update
  []
  (let [f (:update (get levels
                        (Math/min level (dec (count levels)))))]
    (if f (f))))
