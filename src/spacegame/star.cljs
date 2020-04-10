(ns spacegame.star
  (:require [spacegame.config :as cfg]
            [spacegame.globals :as globals :refer [draw-object move-object]]
            [spacegame.drawing :as draw]
            [spacegame.geometry :as geom]))

(def star-shape
  [
   [[-16 0] [0 -16]] [[0 -16] [16 0]] [[16 0] [0 16]] [[0 16] [-16 0]]
   [[-16 0] [-32 0]] [[0 -16] [0 -32]] [[16 0] [32 0]] [[0 16] [0 32]]
   ])

(defn make-star
  [x y]
  {
         :object-type :star
         :id (globals/next-id)
         :x x
         :y y
         :shape star-shape
         :box (geom/bounding-box star-shape)
         :mass 250
   })

(defmethod draw-object :star
  [star]
  (draw/draw-shape (:shape star)
                   :x (:x star)
                   :y (:y star)
                   :colour "yellow"))

(defmethod move-object :star
  [star]
  star)

(defn apply-gravity
  [star target]
  (let [
        x1 (:x star)
        y1 (:y star)
        x2 (:x target)
        y2 (:y target)

        dx (- x2 x1)
        dy (- y2 y1)

        distance (Math/sqrt (+ (* dx dx) (* dy dy)))

        force (/ (:mass star) (* distance distance))
        ]

    [(* force (/ dx distance)) (* force (/ dy distance))]))

(defn apply-gravity-to-all
  [star things]
  (map (fn [thing]
         (let [[fx fy] (apply-gravity star thing)
               fx (if (> (Math/abs fx) 2) (* (Math/sign fx) 2) fx)
               fy (if (> (Math/abs fy) 2) (* (Math/sign fy) 2) fy)]
           
           (assoc thing
                  :dx (- (:dx thing) fx)
                  :dy (+ (:dy thing) fy))))
         things))
