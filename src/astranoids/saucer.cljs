(ns astranoids.saucer
  (:require [astranoids.config :as cfg]
            [astranoids.globals :as globals :refer [draw-object move-object update-object]]
            [astranoids.drawing :as draw]
            [astranoids.geometry :as geom]
            [astranoids.particle :as part]
            [astranoids.bullet :as bullet]))

(def saucer-shape
  [
   [[-8 -2] [-4 -6]] [[-4 -6] [4 -6]] [[4 -6] [8 -2]] [[8 -2] [-8 -2]]
   [[-8 -2] [-20 2]] [[-20 2] [-8 6]] [[-8 6] [8 6]] [[8 6] [20 2]] [[20 2] [8 -2]]
   ])

(defn make-saucer [x y]
  (let [[dx dy] (geom/vector-to-dx-dy (rand globals/circle) (rand 2))]
    {
     :object-type :saucer
     :id (globals/next-id)
     :x x
     :y y
     :dx dx
     :dy dy
     :shape saucer-shape
     :box (geom/bounding-box saucer-shape)
     }))

(defmethod draw-object :saucer
  [saucer]
  (draw/draw-shape (:shape saucer)
                   :x (:x saucer) :y (:y saucer)
                   :colour "yellow"
                   :fill "black"))

(defmethod move-object :saucer
  [saucer]
  (let [x (+ (:x saucer) (:dx saucer))
        y (- (:y saucer) (:dy saucer))
        [w h] cfg/default-canvas-size
        newx (cond (< x 0) w
                   (> x w) 0
                   :else x)
        newy (cond (< y 0) h
                   (> y h) 0
                   :else y)
        ;; Change direction randomly
        [newdx newdy] (if (> (rand 100) 99)
                        (geom/vector-to-dx-dy (rand globals/circle) (rand 2))
                        [(:dx saucer) (:dy saucer)])
        ]
    (assoc saucer :x newx :y newy :dx newdx :dy newdy)))

(defmethod update-object :saucer
  [saucer]
  (if (> (rand 100) 99)
    (globals/add-object :bullets (bullet/make-bullet saucer
                                                     :angle (rand globals/circle)
                                                     :lifetime 300
                                                     :colour "yellow")))
  saucer)

(defn explode
  [saucer]
   (dotimes [_ 20]
     (let [[dx dy] (geom/vector-to-dx-dy (rand globals/circle) (rand 4))]
       (globals/add-object :particles (part/make-particle (:x saucer) (:y saucer)
                                                          dx dy
                                                          :colours ["white" "yellow"])))))
