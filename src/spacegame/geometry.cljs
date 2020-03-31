(ns spacegame.geometry)

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
         bounds [999 999 -999 -999]]
    (if (empty? lines)
      (vector [(nth bounds 0) (nth bounds 1)]
              [(nth bounds 2) (nth bounds 3)])
      (do
        (recur (rest lines) (get-min-max-line (first lines) bounds))))))
