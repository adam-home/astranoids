(ns spacegame.particle
  (:require [spacegame.globals :as globals]))

(def DEFAULT-PARTICLE-COLOURS ["red" "orange" "yellow"])

(defn make-particle [x y dx dy & {:keys [colours]}]
  (let [colour-vec (or colours DEFAULT-PARTICLE-COLOURS)]
    {
     :x x
     :y y
     :dx dx
     :dy dy
     :lifetime 50
     :age 0
     :colour (nth colour-vec
                  (Math/round (rand (dec (count colour-vec)))))
     }))

(defn draw-particle
  [particle]

  (.beginPath globals/buffer-ctx)
  (set! (.-globalAlpha globals/buffer-ctx) (/ (- (:lifetime particle) (:age particle)) (:lifetime particle)))
  (.rect globals/buffer-ctx
         (:x particle) (:y particle)
         2 2)
  (set! (.-fillStyle globals/buffer-ctx) (:colour particle))
  (.fill globals/buffer-ctx))

(defn draw-particles
  [particles]
  (.save globals/buffer-ctx)
  (doseq [p particles]
    (draw-particle p))
  (.restore globals/buffer-ctx))

(defn move-particle
  [particle]
  (assoc particle
         :x (+ (:x particle) (:dx particle))
         :y (- (:y particle) (:dy particle))
         :age (inc (:age particle))))

(defn move-particles
  [particles]
  (let [active (filter #(< (:age %) (:lifetime %)) particles)]
    (map move-particle active)))