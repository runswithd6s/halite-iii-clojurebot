(ns MyBot.ship-tasks
  (:require [hlt.direction :refer [get-all-cardinals]]
            [hlt.log :refer [log]]
            [hlt.random :refer [rrand-int]]
            [hlt.state :refer [constants]]
            [hlt.game :as game]
            [hlt.game-map :as game-map]
            [hlt.map-cell :as map-cell]
            [hlt.player :as player]
            [hlt.position :as position]
            [hlt.ship :as ship]))

(defonce ship-metadata (atom {}))

(defn get-metadata
  "Get metadata on a ship"
  [ship key]
  (get-in @ship-metadata [(ship/id ship) key]))

(defn set-metadata
  "Set metadata on ship"
  [ship key value]
  (log "➥ setting " key " to " value)
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
  (let [cell (game-map/at ship)
        cell-position (map-cell/position cell)
        cell-halite (map-cell/halite-amount cell)]
    (log cell-position "has" cell-halite "halite remaining.")
    (or (= 0 (map-cell/halite-amount cell))
        (> (ship/halite-amount ship) (/ (map-cell/halite-amount cell) 10)))))

(defn return?
  "Should the ship return to a shipyard or depot?"
  [ship]
  (log "➥ has " (ship/halite-amount ship) "halite.")
  (or (and (= (task ship) :return)
           (> 500 (ship/halite-amount ship)))
      (ship/is-full? ship)))

(defn return
  "Return a ship to shipyard"
  [ship]
  (log "➥ returning to shipyard.")
  (let [shipyard (game/me :shipyard :position)
        here (ship/position ship)]
    (update-task ship :return)
    (game/command
     (ship/move ship
      (game-map/get-target-direction here shipyard)))))

(defn gather?
  "Should the ship move from the current spot?"
  [ship cell cell-max-pct]
  (and (can-move? ship)
       (> (map-cell/halite-amount cell) (/ (constants :MAX_HALITE) cell-max-pct))))

;; doesn't work
(defn get-safe-cardinals
  [cell]
  (let [here (map-cell/position cell)]
  (filter #(not (game-map/is-occupied? (game-map/normalize (position/add-position here %)))) (get-all-cardinals))))

(defn gather
  "Gather halite or move to gather halite."
  [ship]
  (let [cell (game-map/at ship)
        target (get (get-all-cardinals) (rrand-int 4))]
    (update-task ship :gather)
    (if (gather? ship cell 10)
      (do
        (log "➥ staying still.")
        (game/command (ship/stay-still ship)))
      (do
        (log "➥ moving randomly.")
        (game/command
         (ship/move ship target))))))

(defn return-or-gather
  "Return to shipyard if full, otherwise gather halite in random directions"
  [ship]
  (if (return? ship)
    (return ship)
    (gather ship)))

