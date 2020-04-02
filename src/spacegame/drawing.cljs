(ns spacegame.drawing
  (:require [spacegame.globals :as globals :refer [buffer-ctx screen-ctx]]))

(defn get-elem-by-id
  [elem-id]
  (-> js/document (.getElementById elem-id)))

(defn create-canvas
  [target-elem-id width height & {:keys [id]}]
  "Create a new canvas element as a child of target-elem with given width and height"
  (let [target-elem (get-elem-by-id target-elem-id)
        canvas-elem (.createElement js/document "canvas")
        buffer (.createElement js/document "canvas")]
    (console.log (str "id=" id))
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

(defn rotate-around-point
  [x y origin-x origin-y angle]
  (let [sa (Math/sin angle)
        ca (Math/cos angle)
        dx (- x origin-x)
        dy (- y origin-y)]

    [(+ (- (* ca dx) (* sa dy)) origin-x)
     (+ (+ (* sa dx) (* ca dy)) origin-y)]))

