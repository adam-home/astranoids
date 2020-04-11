(ns astranoids.config)

;; Discrete step angles
(def steps 64)
(def angles (for [x (range 1 (+ 1 steps))] (* x (/ (* Math.PI 2) steps))))
(def thrust 0.1)
(def max-speed 4)

(def level-message-timeout 250)

(def default-canvas-size [1400 600])
