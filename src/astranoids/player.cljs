(ns astranoids.player
  (:require [astranoids.config :as cfg]
            [astranoids.globals :as globals :refer [draw-object]]
            [astranoids.drawing :as draw]
            [astranoids.geometry :as geom]
            [astranoids.particle :as part]))

;; (def arrow-shape
;;   [[-30 40] [0 -40] [30 40] [0 20]])

(def arrow-shape
  [
   [[-7 10] [0 -10]]
   [[0 -10] [7 10]]
   [[7 10] [0 5]]
   [[0 5] [-7 10]]
  ])

(defn make-player
  []
  (let [[w h] cfg/default-canvas-size
        x (/ w 2)
        y (/ h 2)]
    {
     :object-type :player
     :x x
     :y y
     :dx 0
     :dy 0
     :angle 0
     :angle-idx 0
     :shape arrow-shape
     :lives 3
     :score 0
     :shield 250
     }))

(defn reset-player
  [player]
  (let [new-player (make-player)]
    (assoc new-player
           :score (:score player)
           :lives (:lives player))))

(defn rotate-left
  [player]
  (let [idx (mod (dec (:angle-idx player)) cfg/steps)
        angle (nth cfg/angles idx)]
    (assoc player :angle-idx idx :angle angle)))

(defn rotate-right
  [player]
  (let [idx (mod (inc (:angle-idx player)) cfg/steps)
        angle (nth cfg/angles idx)]
    (assoc player :angle-idx idx :angle angle)))

(defn limit-speed
  [delta-v]
  (if (> (Math/abs delta-v) cfg/max-speed)
    (* (Math.sign delta-v) cfg/max-speed)
    delta-v))
  
(defn thrust
  [player]
  (let [sa (Math/sin (:angle player))
        ca (Math/cos (:angle player))
        x (:x player)
        y (:y player)
        angle (:angle player)
        dx (limit-speed (+ (:dx player) (* cfg/thrust sa)))
        dy (limit-speed (+ (:dy player) (* cfg/thrust ca)))
        [px py] (geom/rotate-around-point x (+ y 10) x y angle)]

    (dotimes [_ 2]
      (let [variance (- (rand 0.5) 0.25)
            [pdx pdy] (geom/vector-to-dx-dy (- angle Math.PI variance) 2)]
        (globals/add-object :particles (part/make-particle px py pdx pdy))))
                                  
    (assoc player :dx dx :dy dy)))

(defn move-player
  [player]
  (let [x (+ (:x player) (:dx player))
        y (- (:y player) (:dy player))
        [w h] cfg/default-canvas-size
        xx (cond (< x 0) w
                 (> x w) 0
                 :else x)
        yy (cond (< y 0) h
                 (> y h) 0
                 :else y)
        shield (:shield player)]

    (assoc player
           :x xx
           :y yy
           :shield (if (> shield 0) (dec shield) 0))))

(defmethod draw-object :player
  [player]
  (draw/draw-shape (:shape player)
                   :colour "yellow"
                   :x (:x player)
                   :y (:y player)
                   :angle (:angle player)
                   :scale 1 :width 2)

  (when (> (:shield player) 0)
    (draw/draw-shield player)))

;; Create a load of particles at the player's location, expanding away
(defn explode
  [player]
  (dotimes [_ 40]
    (let [[dx dy] (geom/vector-to-dx-dy (rand globals/circle) (rand 4))]
      (globals/add-object :particles(part/make-particle (:x player) (:y player)
                                                      dx dy
                                                      :colours ["white" "yellow"])))))
(defn add-score
  [player score]
  (assoc player :score (+ score (:score player))))
