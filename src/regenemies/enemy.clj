;  Copyright (c) Dave Ray, 2012. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns regenemies.enemy)

(declare generate*)

(defn random-char [v depth]
  (rand-nth
    (concat 
      "abcdefghijklmnopqrstuvwxyz"
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      "0123456789"
      "!@#%^&"
      ["\\?" "\\." "\\$" "\\+" "\\*" "\\(" "\\)"])))

(defn random-class [v depth]
  (rand-nth
    ["." "\\w" "\\W" "\\d" "\\D" "\\s" "\\S"]))

(def meta-chars-set #{\? \* \+})
(def meta-chars-vec (vec meta-chars-set))

(defn ends-with-meta?
  [s]
  (or (empty? s) (meta-chars-set (last s))))

(defn random-meta [v depth]
  (if (ends-with-meta? v)
    ""
    (rand-nth meta-chars-vec)))

(defn random-group [v depth]
  (if (pos? depth)
    (str "(" (generate* (dec depth) 4) ")")
    ""))

(def generators
  [random-char
   random-class
   random-meta
   random-group])

(defn generate* [level max-length]
  (loop [result "" length 0]
    (if (or (>= length max-length) 
            (< (rand) (* 0.1 (/ length max-length))))
      result
      (recur (str result ((rand-nth generators) result level))
             (inc length)))))

(defn generate [level max-length]
  (java.util.regex.Pattern/compile (generate* level max-length)))

