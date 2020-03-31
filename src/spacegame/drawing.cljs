(ns spacegame.drawing)

(def current-bg "blue")
(def current-fg "green")

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
  [ctx & {:keys [colour]}]
  "Clears the canvas to :colour, or the current background colour if
  not specified."
    (.beginPath ctx)
    (.rect ctx 0 0 (.-width (.-canvas ctx)) (.-height (.-canvas ctx)))
    (set! (.-fillStyle ctx) (or colour current-bg))
    (.fill ctx))

(defn draw-shape
  [ctx shape & {:keys [colour fill x y angle scale width]}] 
  (.save ctx)

  (when (and x y)
    (.translate ctx x y))
  (when angle
    (.rotate ctx angle))
  (when scale
    (.scale ctx scale scale))
  
  (.beginPath ctx)

  ;; (let [[px py] (first path)]
  ;;   (.moveTo ctx px py))
  
  ;; (doseq [[px py] (rest path)]
  ;;   (.lineTo ctx px py))

  (doseq [path shape]
    (.moveTo ctx (first (first path)) (second (first path)))
    (.lineTo ctx (first (second path)) (second (second path))))
  
  (.closePath ctx)

  (set! (.-lineWidth ctx) (or width 2))
  (set! (.-strokeStyle ctx) (or colour current-fg))
  (when fill
    (set! (.-fillStyle ctx) fill)
    (.fill ctx))
  
  (.stroke ctx)
  
  (.restore ctx))

(defn rotate-around-point
  [x y origin-x origin-y angle]
  (let [sa (Math/sin angle)
        ca (Math/cos angle)
        dx (- x origin-x)
        dy (- y origin-y)]

    [(+ (- (* ca dx) (* sa dy)) origin-x)
     (+ (+ (* sa dx) (* ca dy)) origin-y)]))

