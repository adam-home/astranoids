(ns spacegame.collision
  (:require [spacegame.geometry :as geom]))

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
