(ns spacegame.globals)

(def circle (* Math.PI 2))

(def player nil)

(def scene {})
; (def particles [])

(def level 0)
(def new-level-timer 100)

(def games-played 0)

(def screen-ctx nil)
(def buffer-ctx nil)

(def next-id
  (let [x (atom 0)]
    (fn [] (keyword (str "id-" (swap! x inc))))))

(defmulti draw-object :object-type)
(defmulti move-object :object-type)
