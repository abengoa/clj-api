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

(defmacro with-fn-version [f-name version & body]
	`(binding [~f-name (get-function-version ~(str f-name) ~version)]
		~@body))

(def *_apidefinition_cache_* (atom {}))
(defn get-api [name] (get @*_apidefinition_cache_* name {}))
(defn set-api [name api] (swap! *_apidefinition_cache_* (fn [previous] (assoc previous name api))))
(defn get-api-versions [api] (keys api))
(defn get-api-version [api version] (get api version []))
(defn add-api-version [api version fn-list] (assoc api version fn-list))
(defn clone-api-version [api base-version new-version] (assoc api new-version (get-api-version base-version)))
(defn delete-api-version [api version] (dissoc api version))

(defmacro new-api [name fn-list] `(set-api ~name {:base (map (fn [f] `(str ~f)) fn-list)}))

(defmacro with-api-version
	[api-name version & body]
	`(let [fns# (get-api-version (get-api ~api-name) ~version)]
		(println `(interleave fns# (map #(get-function-version % ~version) fns#)))
		;(interleave fns# 
		;	(for [n fns#] (get-function-version n ~version))))
		;(bindings [~@(interleave fns# (for [n fns#] (get-function-version n ~version)))]
		;~@body)
		))
		
(defmacro redefine-api [api-name version & body]
	;; replace all the (defn f [x] b) for (new-version f version [x] b) 
	;; only when f in api? or all function definitions in the body?
	;; the new function definitions do not overwrite the old
	)