(ns spacegame.particle)

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
  [ctx particle]
  (.beginPath ctx)
  (set! (.-globalAlpha ctx) (/ (- (:lifetime particle) (:age particle)) (:lifetime particle)))
;;  (.rect ctx (:x particle) (:y particle) 4 4)
  (.arc ctx
        (:x particle) (:y particle)
        2
        0
        (* 2 Math.PI))
  (set! (.-fillStyle ctx) (:colour particle))
  (.fill ctx))

(defn draw-particles
  [ctx particles]
  (.save ctx)
  (doseq [p particles]
    (draw-particle ctx p))
  (.restore ctx))

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
