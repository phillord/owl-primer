;; The contents of this file are subject to the LGPL License, Version 3.0.
;;
;; Copyright (C) 2013, Phillip Lord, Newcastle University
;;
;; This program is free software: you can redistribute it and/or modify it
;; under the terms of the GNU Lesser General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or (at your
;; option) any later version.
;;
;; This program is distributed in the hope that it will be useful, but WITHOUT
;; ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
;; FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
;; for more details.
;;
;; You should have received a copy of the GNU Lesser General Public License
;; along with this program. If not, see http://www.gnu.org/licenses/.

(ns owl.primer.family-read
  (:require [tawny owl read render]))


(tawny.read/defread primer
  :location (tawny.owl/iri (clojure.java.io/resource "primer_source.owl"))
  :prefix "fam"
  :iri "http://example.com/owl/families"
  :filter (partial tawny.read/iri-starts-with-filter
                   "http://example.com/owl/families/"))

(def ouch (atom nil))

(do
  (spit "family_render.clj" "")
  (doseq [n (.getSignature primer)]
    (try
      (spit "family_render.clj"
            (str
             (tawny.render/as-form n) "\n") :append true)
      (catch Exception e
        (reset! ouch n)
        (println "Totally Broken on " n)))))

;; this is the one which breaks!!
(comment
  (tawny.render/as-form John))
(doseq [ n (.getClassesInSignature primer)]
  (println n))
