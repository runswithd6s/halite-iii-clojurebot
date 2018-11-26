(ns MyBot
  (:require [hlt.log :refer [log]]
            [hlt.random :refer [set-seed!]]
            [hlt.input :refer [as-int]]
            [hlt.state :refer [constants]]
            [hlt.ship :as ship]
            [MyBot.ship-tasks :as st]
            [hlt.shipyard :as shipyard]
            [hlt.game-map :as game-map]
            [hlt.game :as game])
  (:gen-class))

;; This Clojure API uses kebab-case instead of the snake_case as documented
;; in the API docs. Otherwise the names of methods are analogous.

(defn -main
  "MyBot in Clojure"
  [& args]
  ;; Initialize repeatable random number generator
  (set-seed! (if (seq args) (as-int (first args)) (System/nanoTime)))

  (game/new-game) ;; get initial map data

  ;; At this point the "game" atom is populated with the game state
  ;; This is a good place to do computationally expensive start-up pre-processing.
  ;; After calling ready below, the 2 second per turn timer will start.

  (game/ready "my-clojure-bot")

  ;; Now that your bot is initialized, save a message to yourself in
  ;; the log file with some important information. Here, you log here
  ;; your id, which you can always fetch from the game using my-id

  (log "Successfully created bot! My Player ID is " (game/my-id) ".")

  (while true
    ;; This loop handles each turn of the game. The game state
    ;; changes every turn, and you refresh that state by running update-frame
    (game/update-frame)

    ;; For each ship, return to shipyard if full or gather halite
    (doseq [[_ ship] (game/me :ships)]
      (st/return-or-gather ship))

    ;; If the game is in the first 200 turns and you have enough
    ;; halite, spawn a ship. Don't spawn a ship if you currently have
    ;; a ship at port, though - the ships will collide.
    (when (and (< (game/turn-number) 200)
            (>= (game/me :halite) ;; option 2 destructure inline
              (constants :SHIP_COST))
            (not (game-map/is-occupied? (game/me :shipyard))))
      (log "Spawning a ship!")
      (game/command (shipyard/spawn)))

    ;; above is a naive example of handling a turn

    ;; Send your moves back to the game environment, ending this turn.
    (game/end-turn)))
