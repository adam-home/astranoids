(ns spacegame.collision
  (:require [spacegame.globals :as globals :refer [player]]
            [spacegame.config :as cfg]
            [spacegame.geometry :as geom]
            [spacegame.player :as player]
            [spacegame.asteroid :as asteroid]
            [spacegame.bullet :as bullet]))


;; returns true iff the line from (a,b)->(c,d) intersects with (p,q)->(r,s)
;; function intersects(a,b,c,d,p,q,r,s) {
;;   var det, gamma, lambda;
;;   det = (c - a) * (s - q) - (r - p) * (d - b);
;;   if (det === 0) {
;;     return false;
;;   } else {
;;     lambda = ((s - q) * (r - a) + (p - r) * (s - b)) / det;
;;     gamma = ((b - d) * (r - a) + (c - a) * (s - b)) / det;
;;     return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
;;   }
;; };

;; line definition: [[x1 y1] [x2 y2]]
(defn lines-intersect?
  [line1 line2]
  (let [[a b] (first line1)
        [c d] (second line1)
        [p q] (first line2)
        [r s] (second line2)
        det (- (* (- c a) (- s q))
               (* (- r p) (- d b)))]

    (if (= 0 det)
      false
      (let [lambda (/ (+ (* (- s q) (- r a))
                         (* (- p r) (- s b)))
                      det)
            gamma (/ (+ (* (- b d) (- r a))
                        (* (- c a) (- s b)))
                     det)]

        (and (and (< 0 lambda)
                  (< lambda 1))
             (and (< 0 gamma)
                  (< gamma 1)))))))

(defn get-line-pairs
  [poly1 poly2]
  (for [line1 poly1 line2 poly2]
    (vector line1 line2)))

(defn polygons-intersect?
  [poly1 poly2]
  (let [line-pairs (get-line-pairs poly1 poly2)]
    (loop [pair line-pairs
           intersected false]
      (if (or intersected
              (empty? pair))
        intersected
        (recur (rest pair)
               (lines-intersect? (first (first pair))
                                 (second (first pair))))))))

(defn bullet-intersect?
  [shape box bullet]
  (let [x (:x bullet)
        y (:y bullet)]
    (when (geom/point-in-box? [x y] box)
      (let
          [nw [(- x 2) (- y 2)]
           ne [(+ x 2) (- y 2)]
           se [(+ x 2) (+ y 2)]
           sw [(- x 2) (+ y 2)]
           bullet-shape [
                         [nw ne]
                         [ne se]
                         [se sw]
                         [sw nw]
                         ]]
        (polygons-intersect? shape bullet-shape)))))

(defn bullet-kill-asteroid
  [asteroid bullet]
  ;; Check to see if any of the asteroids have been hit by a bullet
  (set! player (player/add-score player 1))
  (asteroid/explode asteroid)
  (asteroid/remove-asteroid asteroid)
  (bullet/remove-bullet bullet)
  
  (if (> 3 (:generation asteroid))
    (dotimes [_ 2]
      (set! globals/scene
            (update-in globals/scene [:asteroids]
                       (fn [old]
                         (conj old (asteroid/make-asteroid (:x asteroid) (:y asteroid)
                                                               (inc (:generation asteroid))))))))))

(defn check-for-collisions
  []
  (let [player-translated (geom/translate-shape (:shape player) (:x player) (:y player))
        asteroids (:asteroids globals/scene)]
    (doseq [asteroid asteroids]
      (let [asteroid-translated (geom/translate-shape (:shape asteroid) (:x asteroid) (:y asteroid))
            box-translated (geom/translate-box (:box asteroid) (:x asteroid) (:y asteroid)) ]
        
        ;; Check to see if any of the asteroids have hit the player.
        ;; If player has a shield, they are invulnerable.
        (when (and (<= (:shield player) 0)
                   (polygons-intersect? asteroid-translated player-translated))
          (set! player (update player :lives dec))
          (player/explode player)
          (set! player (player/reset-player player)))

        (doseq [bullet globals/bullets]
          (when (bullet-intersect? asteroid-translated box-translated bullet)
            (bullet-kill-asteroid asteroid bullet)))))))

