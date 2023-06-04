(ns events-test
  (:require [cljs.test :refer-macros [testing deftest is]]
            [day8.re-frame.test :as rf-test]))


(deftest hello
      (testing "hello world test"
        (is (= 1 1))))
