;  Copyright (c) Dave Ray, 2012. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns regenemies.test.core
  (:use [regenemies.core]
        [clojure.test]
        [midje.sweet]))

(let [g (new-game)]
  (facts game-over?
    (game-over? g) => false
    (game-over? (assoc g :lives 0)) => true))

(let [g (new-game)]
  (facts missed-pattern?
    (missed-pattern? (assoc g :patterns [{:time-left 9}])) => nil?
    (missed-pattern? (assoc g :patterns [{:time-left -1}])) => truthy))

(let [p (new-pattern #"abc")]
  (facts check-pattern
    (check-pattern p "ab") => p
    (check-pattern p "abc") => (assoc p :ttl 2.0)))

(let [p (-> (new-pattern #"abc") (assoc :time-left 9.0))]
  (facts advance-pattern
    (advance-pattern p 1.0) => (assoc p :time-left 8.0)
    (advance-pattern (assoc p :ttl 0.0) 1.0) => (assoc p :ttl -1.0)))

(let [g (assoc (new-game) :current "abc")]
  (facts backspace-typed
    (:current (backspace-typed g)) => "ab"))

