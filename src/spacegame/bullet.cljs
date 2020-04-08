(ns spacegame.bullet
  (:require [spacegame.config :as cfg]
            [spacegame.globals :as globals :refer [draw-object]]
            [spacegame.drawing :as draw]
            [spacegame.geometry :as geom]))

(defn make-bullet [player]
  (let [
        id (globals/next-id)
        sa (Math/sin (:angle player))
        ca (Math/cos (:angle player))
        dx (+ (:dx player) (* 3 sa))
        dy (+ (:dy player) (* 3 ca))
        [x y] (geom/rotate-around-point (:x player) (- (:y player) 10)
                                        (:x player) (:y player)
                                        (:angle player))]
    {
     :object-type :bullet
     :x x
     :y y
     :dx dx
     :dy dy
     :lifetime 300
     :age 0
     }))

(defn move-bullet
  [bullet]
  (let [x (+ (:x bullet) (:dx bullet))
        y (- (:y bullet) (:dy bullet))
        age (inc (:age bullet))
        [w h] cfg/default-canvas-size
        xx (cond (< x 0) w
                 (> x w) 0
                 :else x)
        yy (cond (< y 0) h
                 (> y h) 0
                 :else y)]
  (assoc bullet :x xx :y yy :age age)))

(defn move-bullets
  [bullets]
  (let [active (filter #(< (:age %) (:lifetime %)) bullets)]
    (into #{} (map move-bullet active))))

(defn remove-bullet
  [bullet]
  (set! globals/bullets (disj globals/bullets bullet)))

(defmethod draw-object :bullet
  [bullet]
  (.beginPath globals/buffer-ctx)
  ;;(.rect ctx (:x bullet) (:y bullet) 4 4)
  (.arc globals/buffer-ctx
        (:x bullet) (:y bullet)
        2
        0
        (* 2 Math.PI))
  
  (set! (.-fillStyle globals/buffer-ctx) "white")
  (set! (.-shadowBlur globals/buffer-ctx) 8)
  (set! (.-shadowColor globals/buffer-ctx) "green")
  (.fill globals/buffer-ctx))
