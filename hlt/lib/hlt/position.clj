(ns hlt.position
  (:require [hlt.direction :as direction]))

(defn add-position
  "adds two positions"
  [a b]
  [(+ (first a) (first b)) (+ (second a) (second b))])

(defn sub-position
  "subtracts position b from a"
  [a b]
  [(- (first a) (first b)) (- (second a) (second b))])

(defn abs
  "Returns absolute value"
  [a]
  (if (neg? a) (- a) a))

(defn abs-position
  "absolute position"
  [position]
  (mapv abs position))

(defn directional-offset
  "returns a new position based on moving one unit in the given
  direction from the given position. This method takes a direction
  such as Direction.West or an equivalent tuple such as (0, -1), but
  will not work with commands such as 'w'."
  [position direction]
  (add-position position direction))

(defn get-surrounding-cardinals
  "returns a list of all positions around the given position in each
  cardinal direction"
  [position]
  (mapv (partial directional-offset position) direction/all-cardinals))

(defn position?
  "Is x a position?"
  [x]
  (and (vector? x) (= 2 (count x)) (int? (first x)) (int? (last x))))

(defn new-position
  "Create a new position"
  [x y]
  [x y])

(defn dir-or-offset
  "Returns direction or a converted offset as a direction."
  [direction]
  (if (position? direction)
    (direction/convert direction)
    direction))
