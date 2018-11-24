(ns hlt.test.position
  (:require [clojure.test :refer :all]
            [hlt.position :refer :all]
            [hlt.direction :as direction]))

(deftest position-tests
  (testing "add-position"
    (is (= (add-position [0 0] [1 1]) [1 1]))
    (is (= (add-position [-3 1] [1 1]) [-2 2])))

  (testing "sub-position"
    (is (= (sub-position [0 0] [1 1]) [-1 -1]))
    (is (= (sub-position [-3 1] [1 1]) [-4 0])))

  (testing "abs"
    (is (= (abs 0) 0))
    (is (= (abs -3) 3)))

  (testing "abs-position"
    (is (= (abs-position [0 0]) [0 0]))
    (is (= (abs-position [1 0]) [1 0]))
    (is (= (abs-position [-3 -8]) [3 8])))

  (testing "directional-offset"
    (let [position [0 1]]
      (is (= (directional-offset position direction/north) [0 0]))
      (is (= (directional-offset position direction/east) [1 1]))
      (is (= (directional-offset position direction/south) [0 2]))
      (is (= (directional-offset position direction/west) [-1 1]))
      (is (thrown? ClassCastException (directional-offset position "w")))))

  (testing "get-surrounding-cardinals"
    (let [position [1 1]
          surrounding-cardinals (get-surrounding-cardinals position)]
      (is (= (some #(= [0 1] %) surrounding-cardinals)))
      (is (= (some #(= [1 0] %) surrounding-cardinals)))
      (is (= (some #(= [1 2] %) surrounding-cardinals)))
      (is (= (some #(= [2 1] %) surrounding-cardinals)))
      (is (= (some #(= [-1 -1] %) (get-surrounding-cardinals [0 0]))))))

  (testing "position?"
    (is (= true (position? [1 2])))
    (is (= false (position? ["n" 2])))
    (is (= false (position? "n"))))

  (testing "new-position"
    (is (= true (position? (new-position 0 0))))
    (is (= false (position? (new-position "a" "b")))))

  (testing "dir-or-offset"
    (let [offset direction/west
          direction "w"]
      (is (= direction (dir-or-offset direction) ))
      (is (= direction (dir-or-offset offset)))))
  )
