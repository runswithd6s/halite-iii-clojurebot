(ns MyBot.ship-tasks
  (:require [hlt.direction :refer [get-all-cardinals]]
            [hlt.game :refer [command me]]
            [hlt.log :refer [log]]
            [hlt.random :refer [rrand-int]]
            [hlt.state :refer [constants]]
            [hlt.game-map :as game-map]
            [hlt.map-cell :as map-cell]
            [hlt.ship :as ship]))

(defonce ship-metadata (atom {}))

(defn get-metadata
  "Get metadata on a ship"
  [ship key]
  (get-in @ship-metadata [(ship/id ship) key]))

(defn set-metadata
  "Set metadata on ship"
  [ship key value]
  (swap! ship-metadata update-in [(ship/id ship)] assoc key value))

(defn target
  "Get target position of a ship"
  [ship]
  (get-metadata ship :target))

(defn update-target
  "Get target position of a ship"
  [ship target]
  (set-metadata ship :target target))


(def tasks [:gather :return :construct])

(defn task
  "Get task of a ship"
  [ship]
  (get-metadata ship :task))

(defn update-task
  "Update a ship task in the game atom"
  [ship task]
  (set-metadata ship :task task))

(defn can-move?
  "Returns true if ship has enough halite in cargo to move out of cell."
  [ship]
  (let [cell (game-map/at ship)]
    (or (= 0 (map-cell/halite-amount cell))
        (> (ship/halite-amount ship) (/ (map-cell/halite-amount cell) 10)))))

(defn return?
  "Should the ship return to a shipyard or depot?"
  [ship]
  (log "Ship:" (ship/id ship)  "has" (ship/halite-amount ship) "halite.")
  (or (and (= (task ship) :return)
           (> 0 (ship/halite-amount ship)))
      (ship/is-full? ship)))

(defn return
  "Return a ship to shipyard"
  [ship]
  (log "Ship:" (ship/id ship) "returning to shipyard.")
  (let [shipyard (me [:shipyard :position])]
    (update-task ship :return)
    (command (ship/move (game-map/naive-navigate shipyard)))))

(defn gather?
  "Should the ship move from the current spot?"
  [ship cell cell-max-pct]
  (and (can-move? ship)
       (> (map-cell/halite-amount cell) (/ (constants :MAX_HALITE) cell-max-pct))))

(defn gather
  "Gather halite or move to gather halite."
  [ship]
  (let [cell (game-map/at ship)
        direction (get (get-all-cardinals) (rrand-int 4))]
    (update-task ship :gather)
    (if (gather? ship cell 10)
      (command (ship/stay-still ship))
      (command
       (ship/move ship (game-map/naive-navigate direction))))))

(defn return-or-gather
  "Return to shipyard if full, otherwise gather halite in random directions"
  [ship]
    (if (return? ship)
      (return ship)
      (gather ship)))

