;  Copyright (c) Dave Ray, 2012. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns regenemies.core
  (:require [regenemies.enemy :as enemy]))

(defn new-pattern
  [p]
  {:regex p
   :ttl nil
   :speed 1.0
   :time-left 10})

(defn new-random-pattern
  []
  (new-pattern (enemy/generate 1 4)))

(defn generate-initial-patterns
  [n]
  (map
    (fn [i] (new-random-pattern))
    (range n)))

(defn new-game
  []
  {:score 0
   :lives 3
   :current ""
   :patterns [] })

(defn new-random-game
  [n]
  (-> (new-game)
    (assoc :patterns (generate-initial-patterns n))))

(defn next-life
  [{:keys [lives score] :as game}]
  (let [new-lives (dec lives)]
    (if (zero? lives)
      (assoc game :lives new-lives)
      (assoc game
             :lives new-lives
             :current ""
             :patterns (generate-initial-patterns (count (:patterns game)))))))

(defn advance-pattern
  [{:keys [ttl] :as p} dt]
  (if ttl
    (assoc p :ttl (- ttl dt))
    (update-in p [:time-left] - dt)))

(defn game-over?
  [{:keys [lives]}]
  (zero? lives))

(defn missed-pattern?
  [{:keys [patterns]}]
  (some #(neg? (:time-left %)) patterns))

(defn replace-dead-pattern [pattern]
  (if (neg? (or (:ttl pattern) 0))
    (new-random-pattern)
    pattern))

(defn advance
  [{:keys [lives patterns] :as game} dt]
  (if (game-over? game)
    game
    (let [new-game (assoc game :patterns
                          (->> patterns
                            (map #(advance-pattern % dt))
                            (map replace-dead-pattern)))
          missed?  (missed-pattern? new-game)
          new-game (if missed? (next-life game) new-game)]
      new-game)))

(defn pattern-matches?
  [{:keys [regex]} input]
  (re-matches regex input))

(defn check-pattern
  [{:keys [ttl regex] :as p} current]
  (if (and (not ttl) (pattern-matches? p current))
    [(assoc p :ttl 1.0) 10]
    [p 0]))

(defn level-up?
  [old-score new-score]
  (and (not= old-score new-score)
       (zero? (rem new-score 100))))

(defn check-patterns
  [game]
  (let [old-score (:score game)
        checked   (map #(check-pattern % (:current game)) (:patterns game))
        new-score (reduce + old-score (map second checked))]
    (assoc game
           :patterns (if (level-up? old-score new-score)
                       (generate-initial-patterns (inc (count checked)))
                       (map first checked))
           :score    new-score)))

(defn character-typed
  [game ch]
  (-> game
    (update-in [:current] str ch)))

(defn enter-typed
  [game]
  (-> game
    check-patterns
    (assoc :current "")))

(defn backspace-typed
  [game]
  (update-in game [:current] #(apply str (butlast %))))

