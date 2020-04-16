(ns astranoids.sound)

(def effects {
              :laser (js/Audio. "sounds/laser.mp3")
              :explosion (js/Audio. "sounds/explosion-small.mp3")
              :explosion-big (js/Audio. "sounds/explosion-big.mp3")
              :saucer (js/Audio. "sounds/saucer.mp3")
              })

(defn play
  [effect]
  (if-let [sound (effect effects)]
    (.play (.cloneNode sound true))))
