(ns hlt.test.dropoff
  (:require [clojure.test :refer :all]
            [hlt.input :refer [read-line-ints]]
            [hlt.dropoff :refer :all]))

(deftest new-dropoff-test
  (testing "new-dropoff creates new structure"
    (let [player-id 11
          dropoff-id 22
          position [0 1]]
      (is (= (new-dropoff player-id dropoff-id position)
             {:hlt.dropoff/id dropoff-id
              :hlt.player/id player-id
              :position position})))))

(deftest read-dropoff-test
  (testing "read-dropoff can read dropoff tuple"
    (let [player-id 3
          dropoff-id 99
          x 2
          y -12]
      (with-redefs
        [read-line-ints (fn [] [dropoff-id x y])]
        (is (= (read-dropoff player-id)
               [dropoff-id
                {:hlt.dropoff/id dropoff-id
                :hlt.player/id player-id
                 :position [x y]}]))))))
