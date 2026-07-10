(ns cartpole-math-test
  (:require [clojure.test :refer [deftest is]]
            [kami.cartpole-math :as cm]))

(deftest canonical-step-stable
  (let [out (cm/canonical-step)]
    (is (= 4 (count out)))
    (is (every? number? out))
    (is (string? (cm/output-hash out)))))
