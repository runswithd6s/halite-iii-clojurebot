(ns hlt.test.commands
  (:require [clojure.test :refer :all]
            [hlt.commands :refer :all]))

(deftest test-commands
  (testing "north"
    (is (= north "n")))
  (testing "south"
    (is (= south "s")))
  (testing "east"
    (is (= east "e")))
  (testing "west"
    (is (= west "w")))
  (testing "stay-still"
    (is (= stay-still "o")))
  (testing "generate"
    (is (= generate "g")))
  (testing "construct"
    (is (= construct "c")))
  (testing "move"
    (is (= move))))
