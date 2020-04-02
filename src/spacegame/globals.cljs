(ns spacegame.globals)

(def circle (* Math.PI 2))

(def player nil)

(def particles [])
(def bullets #{})
(def asteroids #{})

(def level 0)

(def screen-ctx nil)
(def buffer-ctx nil)

(def KEY_LEFT 37)
(def KEY_RIGHT 39)
(def KEY_UP 38)
(def KEY_L_CTRL 17)
(def keys-down #{})

(def next-id
  (let [x (atom 0)]
    (fn [] (keyword (str "id-" (swap! x inc))))))

