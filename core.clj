;;; Clojure and time series learning script

;; Requirements
(ns ts-learning.core
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.random :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [oz.core :as oz :refer :all]
            [clojure.pprint :as pp]
            [clojure.math :as math])
  (:import [org.apache.commons.math3.distribution NormalDistribution]
           [org.apache.commons.math3.random RandomDataGenerator]))

(oz/start-server!)

;; Basic lists of numbers and simple operations
(def ts1 [1.0 2.0 3.0 4.0 5.0]) ; square brackets define a vector
(print ts1) ; prints it

(def ts2 '(1.0 2.0 3.0 4.0 5.0)) ; round brackets are lists
(print ts2)

(assoc ts1 2 999) ; replace a value within a collection.
;; Note that this does not edit ts1 in-place, it returns a new list with element at index 2 replaced.

(reduce + ts1) ; returns the sum of the vector by reducing the + function
(reduce + ts2) ; works on lists too

(rseq ts1) ; reverses the vector
(reverse ts1) ; also reverse. docs say rseq might be faster.

(int 1.0) ; convert a float to an int
(mapv int ts1) ; map over a vector

(odd? 3) ; test oddity
(mapv odd? (mapv int ts1)) ; test oddity in a vector
(filter odd? (mapv int ts1)) ; return only the odd values

(mapv + ts1 ts1) ; vector sum
(mapv + ts1 ts1 ts1) ; works with many
(defn mapv_plus [& args] (partial mapv +)) ; partial is like a functool. The & makes arg counts variable
(mapv_plus ts1 ts1 ts1)

(reduce (partial mapv /) [ts1 ts1 ts1]) ; can use reduce on a partial to apply mapv over a vector of vectors

(subvec ts1 1 2) ; slices like (subvec arg start_inclusive end_exclusive)
(subvec ts1 2) ; no second slice arg means go to the end

;; Random numbers
(def dist (NormalDistribution. 0.0 1.0))
(.sample dist)
(repeatedly 10 #(.sample dist))

;; Some time series functions
(defn lag [ts] (map vector (rest ts) ts)) ; lag operator. Returns a map of value -> lagged value
(lag ts1)

(mapv last (lag ts1)) ; you can get just the lags like this, but note that this is the same as the next line
(pop ts1) ; removes last value and returns. Remember that clojure is purely functional. ts1 isn't mutated.
(rest ts1) ; for reference, rest returns all but the first.

'(ts1)

;; Create a white noise time series
(def series-len 100)
(def white-noise (vec (repeatedly series-len #(.sample dist)))) ;; repeatedly returns a list. vec turns a list into a vector.
(println white-noise)
(println (count white-noise))

;; Plot the white noise
;; I set this up so there's a functional plotter that takes any vector, passes it to
;; map-indexed, and then passes that to oz/view!
(defn give-index [ts]
  (map-indexed (fn [t v] {:time t :value v}) ts))

(pp/print-table [:time :value] (give-index (subvec white-noise 0 10)))

(defn plot-ts! [ts & {:keys [title] :or {title "Time series"}}]
  (oz/view!
   {:data {:values (give-index ts)}
    :mark {:type "line"
           :color "black"
           :strokeWidth 1
           :opacity 1.0}
    :encoding {:x {:field "time" :type "quantitative" :title "Time step"}
               :y {:field "value" :type "quantitative" :title "Value"}}
    :width 600
    :height 300
    :title title}))

(plot-ts! white-noise)

;; AR(1) process
;; Also define a function that makes white noise on-the-fly
(defn make-white-noise [length mean stdev]
  (vec (repeatedly length #(.sample (NormalDistribution. mean stdev)))))

(pp/print-table [:time :value] (give-index (make-white-noise 100 0.0 1.0)))

(defn vec-mean [x]
  (if (empty? x)
    0
    (/ (reduce + x) (count x))))

(println (vec-mean (make-white-noise 100000 1.2 1.0)))

(defn arma [ar-terms ma-terms innovations]
  (let [p (count ar-terms)
        q (count ma-terms)
        dot (fn [cs vs] (reduce + 0 (map * cs vs)))]
    (:ys
     (reduce
      (fn [{:keys [ys eps]} e]
        (let [ar-part (dot ar-terms (reverse (take-last p ys)))
              ma-part (dot ma-terms (reverse (take-last q eps)))
              y (+ ar-part e ma-part)]
          {:ys (conj ys y) :eps (conj eps e)}))
      {:ys [] :eps []} innovations))))

(pp/print-table [:time :value]
                (give-index
                 (arma [0.8] [] (make-white-noise 100 0.0 1.0))))

(plot-ts! (arma [0.67] [0.33] (make-white-noise 100 0.0 25.0)))

;; We can shift a zero-centered ARMA process downwards and then mapv math/exp
;; to produce an exponential ARMA process that resembles a time series of any
;; log-normally distributed process such as air pollution measurements, spectrograms,
;; etc.
(plot-ts! (mapv math/exp 
                (mapv (fn [col] (- col 5)) 
                      (arma [0.67] [0.33] (make-white-noise 1000 0.0 0.67)))))
