(ns owl.primer.family-test
  (:require [owl.primer.family]
            [tawny reasoner fixture])
  (:use [clojure.test]))

(use-fixtures :once (tawny.fixture/reasoner :hermit))

(deftest load-test
  (is true))

(deftest reasoning
  (is (tawny.reasoner/coherent? owl.primer.family/family))
  (is (tawny.reasoner/consistent? owl.primer.family/family)))
