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
(ns owl.primer.family
  (:refer-clojure :only [])
  (:require [owl.primer.family-other :as other])
  (:use [tawny.owl] [tawny.english]))

(defontology family
  :iri "http://example.com/owl/families/"
  :iri-gen #(iri (clojure.core/str "http://example.com/owl/families/" %))
  :noname true)

(owl-import family other/family-other)

(defdproperty hasSSN)

(as-equivalent
 (defclass Human)

 (defclass Person
   :comment "Represents the set of all people."
   :haskey hasSSN))

(as-disjoint
 (defclass Man)
 (defclass Woman))

;; we can put annotations absolutely any where in OWL. Tawny doesn't provide a
;; short cut syntax for all of these. We need to add this to the axiom that we
;; are before we add it to the class
(add-axiom
 (.getOWLSubClassOfAxiom (owl-data-factory)
  Man Person #{(owl-comment "States that every man is a person")}))



(as-inverse
 (defoproperty hasParent)
 (defoproperty hasChild
   :characteristic :asymmetric))

(as-equivalent
 hasChild other/child)

(as-disjoint
 (defoproperty hasSpouse
   :characteristic :symmetric)
 hasParent)

(defoproperty hasWife
  :subproperty hasSpouse
  :domain Man :range Woman)

(defoproperty hasRelative
  :characteristic :reflexive)

(defoproperty parentOf
  :characteristic :irreflexive)

(defoproperty hasHusband
  :characteristic :functional :inversefunctional)

(defoproperty hasAncestor
  :characteristic :transitive)

(defoproperty hasFather
  :subproperty hasParent)

(defoproperty hasBrother)

(defoproperty hasGrandparent
  :subpropertychain hasParent hasParent)

(defoproperty hasUncle
  :subpropertychain hasFather hasBrother)


(as-disjoint
 (defoproperty hasDaughter)
 (defoproperty hasSon))

(defoproperty loves)

(defclass Parent
  :equivalent (some hasChild Person))

(as-disjoint
 (defclass YoungChild)

 (defclass Father :subclass
   (owl-and Man Parent))

 (defclass Mother
   :subclass Woman
   :equivalent (and Woman Person)))

(refine Parent :equivalent
        (or Mother Father))

(defclass ChildlessPerson
  :equivalent (and Person (not Parent))
  :subclass (and Person
                 (not
                  (owl-some
                   (inverse hasParent)
                   (owl-thing)))))

(defclass Grandfather
  :subclass (and Man Parent))

(defclass HappyPerson)
(refine HappyPerson
  :equivalent (and (only hasChild HappyPerson)
                   (some hasChild HappyPerson)))

(defdproperty hasAge
  :domain Person
  :range :XSD_NON_NEGATIVE_INTEGER
  :characteristic :functional)

(as-equivalent hasAge other/age)


(defclass SocialRole)

(defindividual Mary
  :type Person Woman
  :same other/MaryBrown)

(defindividual Susan)

(defindividual Jim)
(defindividual James
  :same Jim)

(defindividual Bill
  :fact
  (fact-not hasWife Mary)
  (fact-not hasDaughter Susan))

;; father is a pun, for which we have no direct support in tawny. We cannot
;; have the same entity twice so call this iFather.
(def iFather
  (individual (.getIRI Father)
              :type SocialRole))

(defindividual Jack
  :type Person (not Parent)
  :fact (fact-not hasAge (literal 53)))

(defindividual John
  :type Father
  (at-least 2 hasChild Parent)
  (exactly 3 hasChild Parent)
  (exactly 5 hasChild)
  (at-most 4 hasChild Parent)
  :same
  other/JohnBrown
  :different Bill
  :fact
  (fact hasWife Mary)
  (fact hasAge 51))


(defclass JohnsChildren
  :equivalent (has-value hasParent John))


(defclass NarcisticPerson
  :equivalent (has-self loves))

(defclass Woman
  :subclass Person)

(defclass Dead)
(defclass Orphan
  :equivalent (only (inverse hasChild) Dead))

(defdatatype personAge
  :equivalent (min-max-inc 0 150))


(defdatatype minorAge
  ;; we use a syntactic variant here rather than min-max-inc
  :equivalent (span >=< 0 18))

(defdatatype majorAge
  :equivalent (owl-and personAge (owl-not minorAge)))

(defdatatype toddlerAge
  :equivalent (oneof 1 2))

(defclass MyBirthdayGuest
  :equivalent (oneof Bill John Mary))

(defclass Teenager
  :subclass (some hasAge (span >=< 13 19))
  )

(defclass Female)
(defindividual Meg)

(defclass  X
  :subclass (and Parent
                 (at-most 1 hasChild)
                 (only hasChild Female))
  :equivalent (and (oneof Mary, Bill, Meg)) Female)

(as-equivalent
 (defclass Adult)
 other/Grownup
 )

(save-ontology "family.omn" :omn)
(save-ontology "family.owl" :owl)
