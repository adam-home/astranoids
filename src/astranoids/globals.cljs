(ns astranoids.globals)

(def circle (* js/Math.PI 2))

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
(defmulti update-object :object-type)

(defn count-objects
  [type]
  (count (type scene)))

(defn add-object
  [type object]
  (doseq [obj (if (seq? object) object (list object))]
    (set! scene (update-in scene [type] (fn [old] (conj old obj))))))

(defn remove-object
  [type object]
  (let [objects (type scene)]
    (set! scene (assoc scene
                       type (remove #(= (:id object) (:id %)) objects)))))

