(ns spacegame.asteroid
  (:require [spacegame.config :as cfg]
            [spacegame.globals :as globals]
            [spacegame.drawing :as draw]
            [spacegame.geometry :as geom]
            [spacegame.particle :as part]))

;; [[-5 -10] [0 -5] [5 -7] [10 0] [7 2] [10 7] [0 10] [-10 5] [-7 0] [-10 -5]])
(def asteroid-shape
  [
   [[-20 -40] [0 -20]]
   [[0 -20] [20 -28]]
   [[20 -28] [40 0]]
   [[40 0] [28 8]]
   [[28 8] [40 28]]
   [[40 28] [0 40]]
   [[0 40] [-40 20]]
   [[-40 20] [-28 0]]
   [[-28 0] [-40 -20]]
   [[-40 -20] [-20 -40]]
   ])

(defn make-asteroid [x y dx dy]
  {
   :id (globals/next-id)
   :x x
   :y y
   :dx dx
   :dy dy
   :shape asteroid-shape
   :box (geom/bounding-box asteroid-shape)
   })

(defn add-asteroids-to-game
  [count]
    (dotimes [_ count]

    (let [x (rand (first cfg/default-canvas-size))
          y (rand (second cfg/default-canvas-size))
          dx (- 1 (rand 2))
          dy (- 1 (rand 2))
          a (make-asteroid x y dx dy)]
      
      ;; (set! globals/asteroids (conj globals/asteroids a))
      (set! globals/asteroids (conj globals/asteroids a))
      )))

(defn draw-asteroid
  [ctx asteroid]
  (draw/draw-shape ctx (:shape asteroid)
                   :x (:x asteroid) :y (:y asteroid)
                   :angle 0 :scale 1 :width 2
                   :color "white"
                   :fill "black"))

(defn draw-asteroids
  [ctx asteroids]
  (.save ctx)
  (doseq [a asteroids]
    (draw-asteroid ctx a))
  (.restore ctx))

(defn move-asteroid
  [asteroid]
  (let [x (+ (:x asteroid) (:dx asteroid))
        y (- (:y asteroid) (:dy asteroid))
        [w h] cfg/default-canvas-size
        xx (cond (< x 0) w
                 (> x w) 0
                 :else x)
        yy (cond (< y 0) h
                 (> y h) 0
                 :else y)]
    (assoc asteroid :x xx :y yy)))

(defn move-asteroids
  [asteroids]
  (into #{} (map move-asteroid asteroids)))

(defn explode
  [ctx asteroid]
  (dotimes [_ 20]
    (let [[dx dy] (geom/vector-to-dx-dy (rand globals/circle) (rand 1))]
      (set! globals/particles (conj globals/particles
                                    (part/make-particle (:x asteroid) (:y asteroid)
                                                        dx dy
                                                        :colours ["green" "yellow"]))))))

(defn remove-asteroid
  [asteroid]
  (set! globals/asteroids (disj globals/asteroids asteroid)))
