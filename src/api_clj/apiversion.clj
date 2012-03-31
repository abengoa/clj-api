(ns api-clj.apiversion)

(def *_apiversion_cache_* (atom {}))

(defn get-function-version [f-name version] (get (get @*_apiversion_cache_* f-name {}) version nil))

(defmacro new-version [fname version fargs & new-fbody]
	`(let [fmap# (get @*_apiversion_cache_* ~(str fname) {})
		  fmap# (if (nil? (get fmap# :base)) (assoc fmap# :base ~fname) fmap#)
		  fmap# (assoc fmap# ~version (fn ~fargs ~@new-fbody))
		  fmap# (assoc fmap# :latest (fn ~fargs ~@new-fbody))]
		  (swap! *_apiversion_cache_* (fn [prev#] (assoc prev# ~(str fname) fmap#)))
		  (defn ~fname ~fargs ~@new-fbody)))

(defmacro with-version [f-name version & body]
	`(binding [~f-name (get-function-version ~(str f-name) ~version)]
		~@body))
