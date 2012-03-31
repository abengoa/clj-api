(ns api-clj.test.apiversion
  (:use [api-clj.apiversion])
  (:use [clojure.test]))

(deftest function-versioning
	(def myfn (fn [x] (inc x)))
	(is 4 (myfn 3))
	(new-version myfn [2 0] [x] (dec x))
	(is 2 (myfn 3))
	(is 4 (with-version myfn :base (myfn 3)))
	(is 2 (with-version myfn [2 0] (myfn 3))))