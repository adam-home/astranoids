(ns spacegame.asteroid
  (:require [spacegame.config :as cfg]
            [spacegame.globals :as globals]
            [spacegame.drawing :as draw]
            [spacegame.geometry :as geom]
            [spacegame.particle :as part]))

;; [[-5 -10] [0 -5] [5 -7] [10 0] [7 2] [10 7] [0 10] [-10 5] [-7 0] [-10 -5]])
(def asteroid-shape-0
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

(def asteroid-shape-1
  [
   [[-10 -20] [0 -10]]
   [[0 -10] [10 -14]]
   [[10 -14] [20 0]]
   [[20 0] [24 4]]
   [[24 4] [20 14]]
   [[20 14] [0 20]]
   [[0 20] [-20 10]]
   [[-20 10] [-14 0]]
   [[-14 0] [-20 -10]]
   [[-20 -10] [-10 -20]]
  ])

(def asteroid-shape-2
  [
   [[-5 -10] [0 -5]]
   [[0 -5] [5 -7]]
   [[5 -7] [10 0]]
   [[10 0] [12 2]]
   [[12 2] [10 7]]
   [[10 7] [0 10]]
   [[0 10] [-10 5]]
   [[-10 5] [-7 0]]
   [[-7 0] [-10 -5]]
   [[-10 -5] [-5 -10]]
   ])

(defn make-asteroid [x y generation]
  (let [shp (case generation
              1 asteroid-shape-0
              2 asteroid-shape-1
              3 asteroid-shape-2
              asteroid-shape-0)
        [dx dy] (geom/vector-to-dx-dy (rand globals/circle) (+ 1 (rand generation)))
        id (globals/next-id)]
    {
     :id id
     :x x
     :y y
     :dx dx
     :dy dy
     :shape shp
     :box (geom/bounding-box shp)
     :generation generation
     }))

(defn make-asteroids
  [count]

  (into #{} (for [_ (range count)]
              (let [x (rand (first cfg/default-canvas-size))
                    y (rand (second cfg/default-canvas-size))
                    dx (- 1 (rand 2))
                    dy (- 1 (rand 2))
                    a (make-asteroid x y 1)]
                a))))

(defn draw-asteroid
  [asteroid]
  (draw/draw-shape (:shape asteroid)
                   :x (:x asteroid) :y (:y asteroid)
                   :angle 0 :scale 1 :width 2
                   :colour "green"
                   :fill "black"))

(defn draw-asteroids
  [asteroids]
  (.save globals/buffer-ctx)
  (doseq [a asteroids]
    (draw-asteroid a))
  (.restore globals/buffer-ctx))

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
  [asteroid]
  (dotimes [_ 20]
    (let [[dx dy] (geom/vector-to-dx-dy (rand globals/circle) (rand 1))]
      (set! globals/particles (conj globals/particles
                                    (part/make-particle (:x asteroid) (:y asteroid)
                                                        dx dy
                                                        :colours ["green" "yellow"]))))))

(defn remove-asteroid
  [asteroid]
  (set! globals/asteroids (disj globals/asteroids asteroid)))
