(ns astranoids.drawing
  (:require [astranoids.config :as cfg]
            [astranoids.globals :as globals :refer [buffer-ctx screen-ctx]]
            [astranoids.geometry :as geom]
            [clojure.string :as str]))

(def alphas {
             \0 [ [[0 0] [8 0]] [[8 0] [8 16]] [[8 16] [0 16]] [[0 16] [0 0]] ]
             \1 [ [[0 0] [4 0]] [[4 0] [4 16]] ]
             \2 [ [[0 0] [8 0]] [[8 0] [8 8]] [[8 8] [0 8]] [[0 8] [0 16]] [[0 16] [8 16]] ]
             \3 [ [[0 0] [8 0]] [[8 0] [8 16]] [[8 16] [0 16]] [[8 8] [0 8]] ]
             \4 [ [[0 0] [0 8]] [[0 8] [8 8]] [[8 0] [8 16]] ]
             \5 [ [[8 0] [0 0]] [[0 0] [0 8]] [[0 8] [8 8]] [[8 8] [8 16]] [[8 16] [0 16]] ]
             \6 [ [[8 0] [0 0]] [[0 0] [0 16]] [[0 16] [8 16]] [[8 16] [8 8]] [[8 8] [0 8]] ]
             \7 [ [[0 0] [8 0]] [[8 0] [8 16]] ]
             \8 [ [[0 0] [8 0]] [[8 0] [8 16]] [[8 16] [0 16]] [[0 16] [0 0]] [[0 8] [8 8]]]
             \9 [ [[8 8] [0 8]] [[0 8] [0 0]] [[0 0] [8 0]] [[8 0] [8 16]] [[8 16] [0 16]] ]

             \space [ ]
             
             \A [ [[0 16] [0 0]] [[0 0] [8 0]] [[8 0] [8 16]] [[0 8] [8 8]] ]
             \B [ [[4 8] [4 0]] [[4 0] [0 0]] [[0 0] [0 16]] [[0 16] [8 16]] [[8 16] [8 8]] [[8 8] [0 8]] ]
             \C [ [[8 0] [0 0]] [[0 0] [0 16]] [[0 16] [8 16]] ]
             \D [ [[0 0] [4 0]] [[4 0] [8 8]] [[8 8] [8 16]] [[8 16] [0 16]] [[0 16] [0 0]] ]
             \E [ [[8 0] [0 0]] [[0 0] [0 16]] [[0 16] [8 16]] [[0 8] [8 8]] ]
             \F [ [[8 0] [0 0]] [[0 0] [0 16]] [[0 8] [8 8]] ]
             \G [ [[8 8] [8 16]] [[8 16] [0 16]] [[0 16] [0 0]] [[0 0] [8 0]] ]
             \H [ [[0 0] [0 16]] [[8 0] [8 16]] [[0 8] [8 8]] ]
             \I [ [[4 0] [4 16]] ]
             \K [ [[0 0] [0 16]] [[0 8] [8 0]] [[0 8] [8 16]] ]
             \L [ [[0 0] [0 16]] [[0 16] [8 16]] ]
             \M [ [[0 16] [0 0]] [[0 0] [8 0]] [[8 0] [8 16]] [[4 0] [4 8]] ]
             \N [ [[0 16] [0 0]] [[0 0] [8 16]] [[8 16] [8 0]] ]
             \O [ [[0 0] [8 0]] [[8 0] [8 16]] [[8 16] [0 16]] [[0 16] [0 0]] ]
             \P [ [[0 16] [0 0]] [[0 0] [8 0]] [[8 0] [8 8]] [[8 8] [0 8]] ]
             \R [ [[0 16] [0 0]] [[0 0] [8 0]] [[8 0] [8 8]] [[8 8] [0 8]] [[0 8] [8 16]] ]
             \S [ [[8 0] [0 0]] [[0 0] [0 8]] [[0 8] [8 8]] [[8 8] [8 16]] [[8 16] [0 16]] ]
             \T [ [[0 0] [8 0]] [[4 0] [4 16]] ]
             \U [ [[0 0] [0 16]] [[0 16] [8 16]] [[8 16] [8 0]] ]
             \V [ [[0 0] [0 8]] [[0 8] [4 16]] [[4 16] [8 8]] [[8 8] [8 0]] ]
             \W [ [[0 0] [0 16]] [[0 16] [8 16]] [[8 16] [8 0]] [[4 16] [4 8]] ]
             \Y [ [[0 0] [4 8]] [[4 8] [4 16]] [[4 8] [8 0]] ]
             })

(defn get-elem-by-id
  [elem-id]
  (-> js/document (.getElementById elem-id)))

(defn create-canvas
  "Create a new canvas element as a child of target-elem with given width and height"
  [target-elem-id width height & {:keys [id]}]
  (let [target-elem (get-elem-by-id target-elem-id)
        canvas-elem (.createElement js/document "canvas")
        buffer (.createElement js/document "canvas")]

    (when id (set! (.-id canvas-elem) id))
    (set! (.-width canvas-elem) width)
    (set! (.-height canvas-elem) height)
    (set! (.-innerHTML target-elem) "")
    (.appendChild target-elem canvas-elem)

    (set! (.-width buffer) width)
    (set! (.-height buffer) height)

    [canvas-elem buffer]))

(defn clear-canvas
  []
  (.beginPath buffer-ctx)
  (.rect buffer-ctx 0 0 (.-width (.-canvas buffer-ctx)) (.-height (.-canvas buffer-ctx)))
  (set! (.-fillStyle buffer-ctx) "black")
  (.fill buffer-ctx))

(defn flip
  []
  (.drawImage screen-ctx
              (.-canvas buffer-ctx)
              0 0))

(defn draw-shape
  [shape & {:keys [colour fill x y angle scale width]}] 
  (.save buffer-ctx)

  (when (and x y)
    (.translate buffer-ctx x y))
  (when angle
    (.rotate buffer-ctx angle))
  (when scale
    (.scale buffer-ctx scale scale))
  
  (.beginPath buffer-ctx)

  (doseq [path shape]
    (.moveTo buffer-ctx (first (first path)) (second (first path)))
    (.lineTo buffer-ctx (first (second path)) (second (second path))))
  
  (.closePath buffer-ctx)

  (set! (.-lineWidth buffer-ctx) (or width 2))
  (set! (.-strokeStyle buffer-ctx) (or colour "white"))
  
  (when fill
    (set! (.-fillStyle buffer-ctx) fill)
    (.fill buffer-ctx))
  
  (.stroke buffer-ctx)
  
  (.restore buffer-ctx))

(defn draw-shield
  [player]

  ;; Shield flickers
  (when (and (> (:shield player) 0)
             (= 0 (mod (:shield player) 5)))
    (.save buffer-ctx)
    (.beginPath buffer-ctx)
    (.arc buffer-ctx (:x player) (:y player) 20 0 globals/circle)
    (set! (.-shadowBlur buffer-ctx) 8)
    (set! (.-shadowColor buffer-ctx) "white")
    (set! (.-lineWidth buffer-ctx) 2)
    (set! (.-strokeStyle buffer-ctx) "white")
    (.stroke buffer-ctx)
    (.restore buffer-ctx)))

(defn draw-lives
  [player]
  (doseq [life (range 0 (:lives player))]
    (draw-shape (:shape player)
                :x (+ 16 (* life 16))
                :y 16)))

(defn draw-char
  [char x y]
  (draw-shape (get alphas char) :x x :y y))

(defn draw-score
  [score]
  (let [str-score (str/reverse (str score))
        digits (count str-score)
        [w _] cfg/default-canvas-size]
    (loop [chars (seq str-score)
           i 0]
      (when (< i digits)
        (draw-char (first chars) (- w (* (inc i) digits 10)) 4)
        (recur (rest chars) (inc i))))))

(defn draw-string
  [text x y]
  (loop [chars (seq text)
         xx x]
    (when-not (empty? chars)
      (draw-char (first chars) xx y)
      (recur (rest chars) (+ 16 xx)))))

(defn draw-string-centre
  [text & { :keys [y y-offset] }]
  (let [[cx cy] (geom/centre-text text)]
    (if y
      (draw-string text cx y)
      (draw-string text cx (+ cy y-offset)))))

(defn draw-string-multiline-offset
  [text y-offset]
  (loop [lines (str/split-lines text)
         yo y-offset]
      
      (when-not (empty? lines)
        (let [[cx cy] (geom/centre-text (first lines))]
          (draw-string (first lines) cx (+ cy yo)))
        (recur (rest lines) (+ 24 yo)))))

(defn draw-objects
  [objects]
  (.save buffer-ctx)
  (doseq [obj objects]
    (globals/draw-object obj))
  (.restore buffer-ctx))

(defn draw-scene
  []
  (.save buffer-ctx)
  (doseq [asteroid (:asteroids globals/scene)]
    (globals/draw-object asteroid))
  (doseq [saucer (:saucers globals/scene)]
    (globals/draw-object saucer))
  (doseq [star (:stars globals/scene)]
    (globals/draw-object star))
  (doseq [bullet (:bullets globals/scene)]
    (globals/draw-object bullet))
  (doseq [particle (:particles globals/scene)]
    (globals/draw-object particle))
  (.restore buffer-ctx))
