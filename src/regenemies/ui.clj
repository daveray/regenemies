;  Copyright (c) Dave Ray, 2012. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns regenemies.ui
  (:use [regenemies.core]
    [seesaw.core])
  (:require [seesaw.graphics :as graphics]
            [seesaw.bind :as b]
            [seesaw.color :as color]))

(def pattern-text-style (graphics/style :foreground :white
                                        :font "CONSOLAS-BOLD-28"))

(def game-over-text-style (graphics/style :foreground :black
                                          :font "ARIAL-BOLD-40"))

(def game-over-fill-style (graphics/style :background (color/color :aliceblue 192)
                                          :font "ARIAL-BOLD-40"))

(def alive-style (graphics/style :background :#124))

(def matching-style (graphics/style :background :#BB4))

(def dead-style (graphics/style :background :darkgreen))

(def countdown-normal-style (graphics/style :foreground :#E55
                                     :font "ARIAL-BOLD-30"))

(def countdown-danger-style (graphics/style :foreground :#F55
                                     :font "ARIAL-BOLD-50"))

(def lives-style (graphics/style :background :green))

(defn paint-pattern
  [g {:keys [regex ttl time-left] :as pattern} input row column width height]
  (let [x (* column width)
        y (* row height)
        matches? (pattern-matches? pattern input)
        dead?    ttl]
    (graphics/draw g
      (graphics/rounded-rect x y (- width 1) (- height 1) 10 10)
      alive-style 

      (graphics/rounded-rect x (+ y (/ height 4)) (- width 1) (/ height 2) 10 10)
      (cond
        dead?    dead-style
        matches? matching-style
        :else    alive-style)

      (graphics/string-shape (+ x 5) (+ y (/ height 2)) (str "/ " regex " /"))
      pattern-text-style

      (graphics/string-shape (+ x 5) (+ y height -5)
                             (cond
                               dead?    "Got it!"
                               matches? (format "%d (hit enter!)" (int time-left))
                               :else    (format "%d" (int time-left))))
      (if (< time-left 3)
        countdown-danger-style
        countdown-normal-style))))

(defn paint-patterns
  [g {:keys [patterns current]} w h]
  (let [pattern-count (count patterns)
        column-count (if (< pattern-count 2) 1 2)
        column-width (quot w column-count)
        row-count  (let [[q r] ((juxt quot rem) pattern-count column-count)]
                     (+ q (if (zero? r) 0 1)))
        row-height (quot h row-count)
        coords (for [column (range column-count)
                     row    (range row-count)]
                 [column row])]
    (doseq [[pattern [column row]] (map vector patterns coords)]
      (paint-pattern g pattern current row column column-width row-height))))

(defn paint-lives
  [g {:keys [lives]} y w h]
  (let [c (+ y (quot h 2))
        r (- (quot h 2) 2)]
    (dotimes [i lives]
      (graphics/draw g
        (graphics/circle (- w (* (inc i) h)) c r)
        lives-style))))

(defn paint-status
  [g {:keys [current score] :as game} w y h]
  (let [prompt? (empty? current)]
    (graphics/draw g
      (graphics/rounded-rect 0 y w h 10 10)
      (graphics/style :background "#333")

      (graphics/string-shape 5 (+ y h -5) (str "> " (if prompt? "Try to match the pattern!" current)))
      (graphics/style :foreground (if prompt? :lightgrey :white) :font "CONSOLAS-BOLD-24")

      (graphics/string-shape 5 (+ h 10) (str score))
      (graphics/style :foreground :white :font "ARIAL-BOLD-40")))
  (paint-lives g game y w h))

(defn paint-game-over
  [c g]
  (graphics/draw g
    (graphics/rect 0 0 (width c) (height c))
    game-over-fill-style

    (graphics/string-shape 5 (quot (height c) 2) "GAME OVER (hit enter)")
    game-over-text-style))

(defn paint
  [c g {:keys [current patterns] :as game}]
  (let [w (width c)
        h (height c)]
    (paint-patterns g game w (- h 30))
    (paint-status g game w (- h 30) 30)
    (if (game-over? game)
      (paint-game-over c g))))

(defn make-canvas []
  (canvas :id :canvas :background :black))

(defn make-frame []
  (frame
    :title      "Regenemies"
    :on-close   :dispose
    :resizable? true
    :size       [800 :by 600]
    :content    (make-canvas)))

(defn handle-key-typed [state-atom c]
  (cond
    (and (game-over? @state-atom) (= \newline c))
      (reset! state-atom (new-random-game 1))

    (= \newline c)
      (swap! state-atom enter-typed)

    (= \backspace c)
      (swap! state-atom backspace-typed)

    (Character/isISOControl  c)
      @state-atom

    :else
      (swap! state-atom character-typed c)))

(defn add-behaviors [root]
  (let [state-atom (atom (new-random-game 1))
        canvas     (select root [:#canvas])
        timer      (timer (fn [_]
                            (swap! state-atom advance 1.0))
                          :delay 1000)]
    (listen root :window-closed (fn [_] (.stop timer)))
    (config! canvas
             :paint (fn [c g] (paint c g @state-atom))
             :focusable? true)

    (listen canvas
      :key-typed (fn [e] (handle-key-typed state-atom (.getKeyChar e))))

    (b/bind
      state-atom
      (b/b-do [state]
            (repaint! canvas)))

    (.requestFocusInWindow canvas)
    root))


(defn -main [& args]
  (-> (make-frame)
    add-behaviors
    show!))

;(dispose! (all-frames))
;(-main)

