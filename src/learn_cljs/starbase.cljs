(ns learn-cljs.starbase
  (:require [bterm.core :as bterm]
            [bterm.io :as io]
            [learn-cljs.starbase.data :as data]
            [goog.dom :as gdom]))

(enable-console-print!)

(def term
  (bterm/attach (gdom/getElement "app")
                {:prompt "=> "
                 :font-size 14}))

(declare on-answer)

(defn prompt
  [game current]
  (let [scene (get game current)
        type (:type scene)]
    (io/clear term)
    (when (or (= :win type)
              (= :lose type))
      (io/print term
                (if (= (:win type))
                  "You've won!"
                  "Game over")))
    (io/println term (:title scene))
    (io/println term (:dialog scene))
    (io/read term #(on-answer game current %))))

(defn on-answer [game current answer]
  (let [scene (get game current)
        next (if (= :skyp (:type answer))
               (:on-continue scene)
               (if (= "yes" answer)
                 (get-in scene [:transitions "yes"])
                 (get-in scene [:transitions "no"])))]
    (prompt game next)))

(prompt data/game :start)

