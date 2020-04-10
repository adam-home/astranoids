(ns spacegame.particle
  (:require [spacegame.globals :as globals :refer [draw-object move-object]]
            [spacegame.config :as cfg]))

(def DEFAULT-PARTICLE-COLOURS ["red" "orange" "yellow"])

(defn make-particle [x y dx dy & {:keys [colours]}]
  (let [colour-vec (or colours DEFAULT-PARTICLE-COLOURS)]
    {
     :object-type :particle
     :x x
     :y y
     :dx dx
     :dy dy
     :lifetime 50
     :age 0
     :colour (nth colour-vec
                  (Math/round (rand (dec (count colour-vec)))))
     }))

(defmethod draw-object :particle
  [particle]

  (.beginPath globals/buffer-ctx)
  (set! (.-globalAlpha globals/buffer-ctx) (/ (- (:lifetime particle) (:age particle)) (:lifetime particle)))
  (.rect globals/buffer-ctx
         (:x particle) (:y particle)
         2 2)
  (set! (.-fillStyle globals/buffer-ctx) (:colour particle))
  (.fill globals/buffer-ctx))

(defmethod move-object :particle
  [particle]
  (let [x (+ (:x particle) (:dx particle))
        y (- (:y particle) (:dy particle))
        age (inc (:age particle))
        [w h] cfg/default-canvas-size
        xx (cond (< x 0) w
                 (> x w) 0
                 :else x)
        yy (cond (< y 0) h
                 (> y h) 0
                 :else y)]
    (if (> age (:lifetime particle))
      nil
      (assoc particle :x xx :y yy :age age))))
