(ns learn-cljs.starbase
  (:require [bterm.core :as bterm]
            [bterm.io :as io]
            [learn-cljs.starbase.data :as data]
            [goog.dom :as gdom]))

(enable-console-print!)

(def state (atom :none))

(def term
  (bterm/attach (gdom/getElement "app")
                {:prompt "=> "
                 :font-size 14}))

(declare on-answer)

(defn prompt
  [game current]
  (let [scene (if (= :none @state)
                (get game current)
                (get game @state))
        type (:type scene)]
    (io/clear term)
    (when (or (= :win type)
              (= :lose type))
      (io/print term
                (if (= :win type)
                  "You've won!"
                  "Game over")))
    (io/println term (:title scene))
    (io/println term (:dialog scene))
    (io/read term #(on-answer game current %))))

(defn show-help
  [game current]
  (io/clear term)
  (io/println term (str "List of options:\n"
                        "easter-egg\n"
                        "help\n"
                        "restart\n"
                        "save\n"
                        "yes\n"
                        "no\n"))
  (io/read term #(on-answer game current %)))

(defn save-state
  [game current]
  (io/clear term)
  (reset! state current)
  (io/read term #(on-answer game current %)))

(defn print-nave []
  "
           /\\
          |==|
          |  |
         /____\\
        |      |
        |  ||  |
        |  ||  |
        |__||__|
       /_oooooo_\\
      /__________\\
     |============|
     |   SPACE    |
     |  EXPLORER  |
     '------------'
        |    |
       /|____|\\
      /_/    \\_\\
  ")

(defn rickroll
  [game current]
  (io/clear term)
  (io/println term (print-nave))
  
  (io/read term #(on-answer game current %)))

(defn on-answer [game current answer]
  (let [scene (get game current)
        next-state (if (= :skip (:type answer))
                     (:on-continue scene)
                     (if (= "yes" answer)
                       (get-in scene [:transitions "yes"])
                       (get-in scene [:transitions "no"])))]
    (condp = answer
      "easter-egg" (rickroll game current)
      "help"       (show-help game current)
      "restart"    (prompt game :start)
      "save"       (save-state game current)
      (prompt game next-state))))

(prompt data/game :start)

