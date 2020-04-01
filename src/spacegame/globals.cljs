(ns spacegame.globals)

(def circle (* Math.PI 2))

(def particles [])
(def bullets #{})
(def asteroids #{})

(def level 0)

(def screen-ctx nil)
(def buffer-ctx nil)

(def next-id
  (let [x (atom 0)]
    (fn [] (keyword (str "id-" (swap! x inc))))))

