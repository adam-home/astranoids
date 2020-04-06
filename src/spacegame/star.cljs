(ns spacegame.star
  (:require [spacegame.config :as cfg][spacegame.globals :as globals]
            [spacegame.drawing :as draw]
            [spacegame.geometry :as geom]))

(def star-shape
  [
   [[-16 0] [0 -16]] [[0 -16] [16 0]] [[16 0] [0 16]] [[0 16] [-16 0]]
   [[-16 0] [-32 0]] [[0 -16] [0 -32]] [[16 0] [32 0]] [[0 16] [0 32]]
   ])

(defn make-star
  [x y]
  (list {
         :id (globals/next-id)
         :x x
         :y y
         :shape star-shape
         :box (geom/bounding-box star-shape)
         }))

(defn draw-star
  [star]
  (draw/draw-shape (:shape star)
                   :x (:x star)
                   :y (:y star)
                   :colour "yellow"))

(defn draw-stars
  [stars]
  (.save globals/buffer-ctx)
  (doseq [s stars]
    (draw-star s))
  (.restore globals/buffer-ctx))

(defn get-distance
  [obj1 obj2]
  (let [[x1 y1] obj1
        [x2 y2] obj2

        dx (Math/abs (- x2 x1))
        dy (Math/abs (- y2 y1))]

        (Math/sqrt (+ (* dx dx) (* dy dy)))))
    

(defn apply-gravity
  [star target]
  (let [distance (get-distance [(:x star) (:y star)]
                               [(:x target) (:y target)])]
    
    
    ))
