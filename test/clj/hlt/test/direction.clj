(ns hlt.test.direction
  (:require [clojure.test :refer :all]
            [hlt.direction :refer :all]
            [hlt.commands :as commands]))

(deftest get-all-cardinals-test
  (testing "get-all-cardinals"
    (is (= (get-all-cardinals) all-cardinals))))

(deftest convert-test
  (testing "convert"
    (is (= (convert north) commands/north))
    (is (= (convert south) commands/south))
    (is (= (convert east) commands/east))
    (is (= (convert west) commands/west))
    (is (= (convert still) commands/stay-still))))

(deftest invert-test
  (testing "invert"
    (is (= (invert north) south))
    (is (= (invert south) north))
    (is (= (invert east) west))
    (is (= (invert west) east))
    (is (= (invert still) still))))
