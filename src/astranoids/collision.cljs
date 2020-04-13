(ns astranoids.collision
  (:require [astranoids.globals :as globals :refer [player]]
            [astranoids.config :as cfg]
            [astranoids.geometry :as geom]
            [astranoids.player :as player]
            [astranoids.asteroid :as asteroid]
            [astranoids.saucer :as saucer]
            [astranoids.bullet :as bullet]))


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

(defn boxes-intersect?
  [box1 box2]
  (let [[[b1x1 b1y1] [b1x2 b1y2]] box1
        [[b2x1 b2y1] [b2x2 b2y2]] box2]
    (or (geom/point-in-box? [b1x1 b1y1] box2)
        (geom/point-in-box? [b1x2 b1y2] box2)
        (geom/point-in-box? [b1x1 b1y2] box2)
        (geom/point-in-box? [b1x2 b1y1] box2)
        (geom/point-in-box? [b2x1 b2y1] box1)
        (geom/point-in-box? [b2x2 b2y2] box1)
        (geom/point-in-box? [b2x1 b2y2] box1)
        (geom/point-in-box? [b2x2 b2y1] box1))))

(defn polygons-intersect?
  ([poly1 poly2 box1 box2]
   (and (boxes-intersect? box1 box2)
        (polygons-intersect? poly1 poly2)))
  ([poly1 poly2]
   (let [line-pairs (get-line-pairs poly1 poly2)]
     (loop [pair line-pairs
            intersected false]
       (if (or intersected
               (empty? pair))
         intersected
         (recur (rest pair)
                (lines-intersect? (first (first pair))
                                  (second (first pair)))))))))

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
  (globals/remove-object :asteroids asteroid)
  (bullet/remove-bullet bullet)
  
  (if (> 3 (:generation asteroid))
    (dotimes [_ 2]
      (globals/add-object :asteroids (asteroid/make-asteroid (:x asteroid) (:y asteroid)
                                                             (inc (:generation asteroid)))))))

(defn check-for-collisions
  []
  (let [[player-translated player-hit-box] (geom/translate-shape-and-box player)]

    ;; Asteroids vs player
    ;; Asteroids vs saucers
    ;; Asteroids vs bullets
    (doseq [asteroid (:asteroids globals/scene)]
      (let [[asteroid-translated asteroid-hit-box] (geom/translate-shape-and-box asteroid)]

        ;; Check to see if the asteroid has hit the player.
        ;; If player has a shield, they are invulnerable.
        (when (and (<= (:shield player) 0)
                   (polygons-intersect? asteroid-translated player-translated asteroid-hit-box player-hit-box))
          (set! player (update player :lives dec))
          (player/explode player)
          (set! player (player/reset-player player)))

        ;; Have any saucers been hit by the asteroid?
        (doseq [saucer (:saucers globals/scene)]
          (let [[saucer-translated saucer-hit-box] (geom/translate-shape-and-box saucer)]
            (when (polygons-intersect? asteroid-translated saucer-translated asteroid-hit-box saucer-hit-box)
              (saucer/explode saucer)
              (globals/remove-object :saucers saucer))))
        
        ;; Have any bullets hit the asteroid?
        (doseq [bullet (:bullets globals/scene)]
          (when (bullet-intersect? asteroid-translated asteroid-hit-box bullet)
            (bullet-kill-asteroid asteroid bullet)))))
    
    ;; Saucers vs Player
    ;; Player bullets vs saucers
    ;; Saucer bullets vs player
    (doseq [saucer (:saucers globals/scene)]
      (let [[saucer-translated saucer-hit-box] (geom/translate-shape-and-box saucer)]

        ;; Saucers vs player
        (when (polygons-intersect? saucer-translated player-translated saucer-hit-box player-hit-box)
          (saucer/explode saucer)
          (globals/remove-object :saucers saucer)
          (when (<= (:shield player) 0)
            (set! player (update player :lives dec))
            (player/explode player)
            (set! player (player/reset-player player))))

        ;; Saucers vs player bullets
        (doseq [bullet (filter #(= :player (:object-type (:owner %)))
                               (:bullets globals/scene))]
          (when (bullet-intersect? saucer-translated saucer-hit-box bullet)
            (bullet/remove-bullet bullet)
            (saucer/explode saucer)
            (globals/remove-object :saucers saucer)
            (set! player (player/add-score player 10))))

        ;; Player vs saucer bullets
        (doseq [bullet (filter #(= :saucer (:object-type (:owner %)))
                               (:bullets globals/scene))]
          (when (bullet-intersect? player-translated player-hit-box bullet)
            (bullet/remove-bullet bullet)
            (set! player (update player :lives dec))
            (player/explode player)
            (set! player (player/reset-player player))))
      ))))

