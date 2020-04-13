(ns astranoids.bullet
  (:require [astranoids.config :as cfg]
            [astranoids.globals :as globals :refer [draw-object move-object]]
            [astranoids.drawing :as draw]
            [astranoids.geometry :as geom]))

(defn make-bullet [owner & { :keys [angle lifetime colour] }]
  (let [
        rot (or angle (:angle owner))
        id (globals/next-id)
        sa (Math/sin rot)
        ca (Math/cos rot)
        dx (+ (:dx owner) (* 3 sa))
        dy (+ (:dy owner) (* 3 ca))
        [x y] (geom/rotate-around-point (:x owner) (- (:y owner) 10)
                                        (:x owner) (:y owner)
                                        rot)]
    {
     :object-type :bullet
     :id id
     :owner owner
     :x x
     :y y
     :dx dx
     :dy dy
     :lifetime (or lifetime 300)
     :age 0
     :colour (or colour "white")
     }))

(defmethod move-object :bullet
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
    (if (> age (:lifetime bullet))
      nil
      (assoc bullet :x xx :y yy :age age))))

(defn remove-bullet
  [bullet]
  (let [bullets (:bullets globals/scene)]
    (set! globals/scene
          (assoc globals/scene
                 :bullets (remove #(= (:id bullet) (:id %)) bullets)))))

(defmethod draw-object :bullet
  [bullet]
  (.beginPath globals/buffer-ctx)
  ;;(.rect ctx (:x bullet) (:y bullet) 4 4)
  (.arc globals/buffer-ctx
        (:x bullet) (:y bullet)
        2
        0
        (* 2 Math.PI))
  
  (set! (.-fillStyle globals/buffer-ctx) (:colour bullet))
  (set! (.-shadowBlur globals/buffer-ctx) 8)
  (set! (.-shadowColor globals/buffer-ctx) "green")
  (.fill globals/buffer-ctx))

(defn get-bullets-owned-by-type
  [type]
  (filter #(= type (:object-type (:owner %))) (:bullets globals/scene)))
