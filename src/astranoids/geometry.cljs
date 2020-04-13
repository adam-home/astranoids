(ns astranoids.geometry
  (:require [astranoids.config :as cfg]))

(defn- translate-pair
  [pair dx dy]
  (vector (+ dx (first pair))
          (+ dy (second pair))))

(defn- translate-line
  [line dx dy]
  (vec (for [pair line]
         (translate-pair pair dx dy))))

(defn translate-shape
  [shape dx dy]
  (vec (for [line shape]
         (translate-line line dx dy))))

(defn translate-box
  [box dx dy]
  (for [pair box]
    (translate-pair pair dx dy)))

(defn rotate-around-point
  [x y origin-x origin-y angle]
  (let [sa (Math/sin angle)
        ca (Math/cos angle)
        dx (- x origin-x)
        dy (- y origin-y)]

    [(+ (- (* ca dx) (* sa dy)) origin-x)
     (+ (+ (* sa dx) (* ca dy)) origin-y)]))

(defn vector-to-dx-dy
  [angle velocity]
  (let [dx (* velocity (Math/sin angle))
        dy (* velocity (Math/cos angle))]
    [dx dy]))

(defn get-min-max-line
  [points bounds]
  (let [[x1 y1] (first points)
        [x2 y2] (second points)
        [minx miny maxx maxy] bounds]
    [(Math/min x1 x2 minx) (Math/min y1 y2 miny)
     (Math/max x1 x2 maxx) (Math/max y1 y2 maxy)]))

(defn point-in-box?
  [point box]
  (let [[px py] point
        [[bx1 by1] [bx2 by2]] box]
    (and (>= px bx1)
         (>= py by1)
         (<= px bx2)
         (<= py by2))))

(defn bounding-box
  "Find bounding box for shape"
  [shape]
  (loop [lines shape
         bounds [99999 99999 -99999 -99999]]
    (if (empty? lines)
      (vector [(nth bounds 0) (nth bounds 1)]
              [(nth bounds 2) (nth bounds 3)])
      (do
        (recur (rest lines) (get-min-max-line (first lines) bounds))))))

(defn centre-text
  [text]
  (let [[w h] cfg/default-canvas-size
        digits (count text)
        text-width (+ (* 8 digits) (* 8 (dec digits)))
        x (/ (- w text-width) 2)
        y (/ (- h 16) 2)]
        [x y]))

(defn translate-shape-and-box
  [object]
  (let [obj-translated (translate-shape (:shape object) (:x object) (:y object))
        obj-hit-box (translate-box (or (:box object)
                                       (bounding-box (:shape object)))
                                   (:x object) (:y object))]
    [obj-translated obj-hit-box]))

