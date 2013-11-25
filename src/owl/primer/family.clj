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

;; # Copyright
;;
;; This ontology has been translated by hand and automatically from the OWL
;; Primer ontology which is available at http://www.w3.org/TR/owl2-primer/.
;; The copyright of that work lies with W3C.

;; # Disclaimer
;;
;; This file is not an official W3C specification nor is it produced by W3C.
;; It has been written to demonstrate the use of the Tawny-OWL library over a
;; familiar ontology.

;; # Introduction
;;
;; The W3C OWL specification is designed to represent rich knowledge about a
;; domain. The OWL Primer is a accessible introduction to this specification
;; and provides a strong exemplar for the use of OWL. The ontology that it
;; presents is particularly useful because it uses most (or all) of the
;; expressivity of OWL2.

;; This document shows the use of the
;; [Tawny-OWL](http://github.com/phillord/tawny-owl) library to generate a
;; version of the OWL Primer ontology. Tawny-OWL provides an ontology
;; development environment with a syntax is simple to use but which is, none
;; the less, full programmatic.


;; # Namespace Declaration
;;
;; This is a Clojure namespace declaration and has no effect on the external
;; semantics of the OWL file generated. Here we block the automatic `use` of
;; the `clojure.core` namespace, and `require` both `tawny.owl` and
;; `tawny.english`. This alows use of the simpler English names of `and`
;; rather than `owl-and`; normally these name-clash `clojure.core` functions.

;; Additionally, we require `family-other` which provides a secondary ontology
;; that we will use later. Note that this again only has semantics in Clojure
;; space, and does not force an automatic OWL import.
(ns owl.primer.family
  (:refer-clojure :only [])
  (:require [owl.primer.family-other :as other])
  (:use [tawny.owl] [tawny.english]))


;; # General Issues
;;
;; Tawny-OWL provides a number of functions to define new entities in an
;; ontology. These functions all keyword arguments of the form `(function :a
;; arg1 arg2 arg2 :b arg1 arg2)`. Arguments can be actually be any form of
;; Clojure data structure, and usually flattened before use. We call these
;; "frames", after the common ontology usage.
;;
;; As used in this file, Tawny-OWL forces declaration of entities before use.
;; This behaviour is enforced as a byproduct of the use of Clojure symbols for
;; these entities. The chief advantage of this is that is prevents ontology
;; developer error; it comes with a cost of enforcing a somewhat arbitrary
;; order on the ontology (simplest to complex) rather than a more narrative
;; structure. It is possible to avoid this by using the non-interning forms
;; and strings: so `(defclass A)` becomes `(owl-class "A")`.

;; # Ontology Definition and Imports

;; Here we define an new ontology with an IRI after the OWL Ontology. The
;; Clojure label `family` allows us to refer to this ontology within Clojure
;; and has no other semantics associated with it.

;; The arguments are as follows:
;;
;;  * :iri defines the base IRI for this ontology
;;  * :iri-gen the OWL Primer ontology uses a IRI scheme with term names after
;;    a final slash, as opposed to the Tawny-OWL default of adding a `#`, so
;;    we need to modify the iri-gen function.
;;  * :noname tawny normally adds annotation describing the name used for a
;;    entity in Clojure space which, here, we block.
(defontology family
  :iri "http://example.com/owl/families/"
  :iri-gen #(iri (clojure.core/str "http://example.com/owl/families/" %))
  :noname true)

;; The OWL Primer makes uses of a secondary ontology; we have defined that
;; ontology also, and import it here. Note that we have also `require`d the
;; ontology in the Clojure namespace declaration above.
(owl-import family other/family-other)

;; # Humans
;;
;; First we define the datatype property `hasSSN` which we will use in the
;; definition of Person. Datatype properties describe a relationship between
;; an individual and some piece of data. Here, we do not define the type of
;; data any further.

(defdproperty hasSSN)

;; `Human` and `Person` are two alternative classes which are equivalent. We
;; use the `as-equivalent` form here, as it allows us to make the two mutually
;; equivalent. The alternative, would require a redefinition of at least one
;; as they refer to each other:
;;
;;     (defclass Human)
;;     (defclass Person :equivalent Human)
;;     (owl-class Human :equivalent Person)
;;
;; Semantically, equivalent is symmetrical, so strictly, we do not need the
;; mutual equivalance. The equivalence is being used here to approximate a
;; synonym; we could also have achieved this solely in Clojure using:
;;
;;     (defclass Human)
;;     (def Person Human)
;;
;; which would allow us to refer to either `Person` or `Human` in Clojure, but
;; not in OWL.
;;
;; We add a comment to `Person`. Tawny-OWL provides shortcut frames for
;; comments and labels which are more succinct; however, these assume English;
;; the alternative `:annotation` frame allows other languages. So
;;
;;     (defclass Person
;;         :annotation (owl-comment "Tutti persona" "it"))
;;
;; adds an Italian specification. It is possible to add new frames
;; programmatically, so shortcut frames can be internationalised.
(as-equivalent
 (defclass Human)

 (defclass Person
   :comment "Represents the set of all people."
   :haskey hasSSN))

;; We define the `hasAge` dataproperty, and restrict its domain to Person --
;; that is only a person can have an age. The range is restricted to non
;; negative integer; the name here comes from the `OWL2Datatype` enum in the
;; [OWL
;; API](http://owlapi.sourceforge.net/javadoc/org/semanticweb/owlapi/vocab/OWL2Datatype.html),
;; "keywordized" with a `:`. Finally, we add the `functional` characteristic --
;; an individual person can have one and only one age.
(defdproperty hasAge
  :domain Person
  :range :XSD_NON_NEGATIVE_INTEGER
  :characteristic :functional)

;; `hasAge` is equivalent to `other/age` which is defined in an imported
;; ontology. We could have put the `as-equivalent` form around the
;; `defdproperty` form for the same semantics. However, this feels more
;; natural, as equivalence is being used as a mapping here.
(as-equivalent hasAge other/age)

;; Here we define two classes, namely `Man` and `Woman`. These two are
;; declared as disjoint -- that is, it is not possible to be a man and woman
;; at the same time. `Woman` is defined as a `Person` (`Man` will be in the
;; next expression).
(as-disjoint
 (defclass Man)
 (defclass Woman :subclass Person))


;; It is possible to add annotations to many different statements in OWL, and
;; this does not necessarily fit naturally with Tawny syntax. So, specific
;; support is not provided for adding annotation to the statement that `Man`
;; is a subclass of `Person` (it *is* possible to add annotations to either
;; `Man` or `Person`). Philosophically, however, tawny takes the stance of
;; "embracing the platform": we have build on the OWL API, adding only
;; dynamism, and an extensible syntax where it is convienient, but not
;; adding syntactic wrappers for the sake of it. So, we add the statement that
;; `Man` is a subclass of `Person` and add an annotation.
(add-axiom
 (.getOWLSubClassOfAxiom (owl-data-factory)
  Man Person #{(owl-comment "States that every man is a person")}))

;; Here we define our first two object properties -- relationships from one
;; individual to another. As with equivalence, we use `as-inverse` to avoid
;; the two properties having to refer to each other. Additionally, we define
;; `hasChild` as asymmetric -- that is if an individual has a child, their child
;; cannot have them as a child as well.
(as-inverse
 (defoproperty hasParent)
 (defoproperty hasChild
   :characteristic :asymmetric))

;; Again, we use equivalence to determine a mapping between this ontology and
;; an external one. We could also have added an `:equivalent` frame to the
;; form above.
(as-equivalent
 hasChild other/child)

;; we define a new property called `hasSpouse`. This is symmetric -- if `a
;; hasSpouse b`, then `b hasSpouse a` also. `hasParent` which we defined
;; previously, is disjoint -- your parent cannot be your spouse. Here, we have
;; used a `:disjoint` frame, although we could also have used an `as-disjoint`
;; form.
(defoproperty hasSpouse
  :characteristic :symmetric
  :disjoint hasParent)


;; We define `hasWife` as a subproperty of `hasSpouse` -- that is, if you have
;; a wife, you also have a spouse; more over, you must be a man to have a
;; wife. All ontologies represent a point-in-time, and domain of this property
;; is now factually incorrect in several jurisdictions.
(defoproperty hasWife
  :subproperty hasSpouse
  :domain Man :range Woman)

;; We define `hasHusband`, with a `:functional` characteristic -- so an
;; individual can have only one husband. We also add a
;; `:inversefunctional` characteristic; so, an individual can only be a
;; husband to one other. This is also factually incorrect in some
;; jurisdictions. Even where it is correct, it means that this ontology
;; implicitly uses a "point-in-time"; most jurisdictions allow multiple
;; husbands, just not at the same time.
(defoproperty hasHusband
  :characteristic :functional
  :inversefunctional)

;; We define `hasRelative` as `:reflexive`, so every individual has themselves
;; as a relative.
(defoproperty hasRelative
  :characteristic :reflexive)

;; While `parentOf` is irreflexive -- you cannot be your own parent.
(defoproperty parentOf
  :characteristic :irreflexive)

;; And hasAncestor is transitive. Your ancestors ancestor is also your ancestor.
(defoproperty hasAncestor
  :characteristic :transitive)

;; These are straightforward, using semantics we have already used before.
(defoproperty hasFather
  :subproperty hasParent)

(defoproperty hasBrother)

;; `hasGrandparent` has a subpropertychain, which means that if `a hasParent b
;; hasParent c` implies `a hasGrandparent c`. Likewise, for uncle. If we need
;; to specify more than one subpropertychain, they can be placed in vectors --
;;
;;     (defoproperty r :subpropertychain [a b][c d])
;;
;; says that `r` has is the subproperty of `a`, `b` and `c`, `d`.
(defoproperty hasGrandparent
  :subpropertychain hasParent hasParent)

(defoproperty hasUncle
  :subpropertychain hasFather hasBrother)

;; These are straightforward and use only semantics we have seen before
(as-disjoint
 (defoproperty hasDaughter)
 (defoproperty hasSon))

(defoproperty loves)

;; Here we define our first existential relationship, which says a parent is
;; equivalent to someone with a least one child; so anyone with a child is a
;; parent. The use of `some` here name clashes with the `clojure.core`. The
;; use of `owl-some` is longer but safer.
(defclass Parent
  :equivalent (some hasChild Person))

;; Three disjoint classes. Here we use both `and` and `owl-and`; these refer
;; to the same function. The choice between them depends on how programmatic
;; an environment is needed. If not, then `and` is less typing. If so, then
;; the requirement to either use namespace qualification or `owl-and` means a
;; little more namespace qualification but the more usual logical `and` is
;; still freely available.
(as-disjoint
 (defclass YoungChild)

 (defclass Father :subclass
   (owl-and Man Parent))

 (defclass Mother
   :subclass Woman
   :equivalent (and Woman Parent)))

;; This is our first use of `refine`. It adds more frames to an existing
;; entity. In this case, `owl-class` could have been used equivalently. The
;; advantage of `refine` is that it works for any entity. In this case, a
;; covering axiom is being added to `Parent`. We are using a slightly odd
;; ontological construction here as `Mother` and `Father` could have been
;; defined using `as-subclasses` with `:disjoint` and `:cover` options.
(refine Parent :equivalent
        (or Mother Father))

;; This is the first use of an anonymous `inverse` which describes the inverse
;; of a property without actually having to name it.
(defclass ChildlessPerson
  :equivalent (and Person (not Parent))
  :subclass (and Person
                 (not
                  (owl-some
                   (inverse hasParent)
                   (owl-thing)))))

;; All grandfathers are men who are parents.
(defclass Grandfather
  :subclass (and Man Parent))

;; We could also have defined this with the tawny `some-only` which is
;; logically the same thing. We use `defclass` and `refine` as we need to
;; define `HappyPerson` before using it.
(defclass HappyPerson)
(refine HappyPerson
        :equivalent (and (only hasChild HappyPerson)
                         (some hasChild HappyPerson)))

(defclass SocialRole)

;; This is the first individual we have defined. In this case, we define it
;; with two types, `Person` and `Woman`, and we state that it is the same
;; individual as `MaryBrown` which is defined in a separate ontology.
(defindividual Mary
  :type Person Woman
  :same other/MaryBrown)

(defindividual Susan)

;; `Jim` and `James` are also defined as the same individual, although
;; probably multiple labels would make more sense in this case.
(defindividual Jim)
(defindividual James
  :same Jim)

;; We define another individual and give two facts -- these state explicit
;; relationships between this individual and some other. In this case, we
;; state that `Bill` does not have `Mary` as a wife, nor `Susan` as a
;; daughter.
;;
;; The syntax of the :fact frame is somewhat unwieldy, and it is a candiate
;; for later improvement, perhaps using vectors or maps and introducing a
;; `:fact-not` frame.
(defindividual Bill
  :fact
  (fact-not hasWife Mary)
  (fact-not hasDaughter Susan))

;; OWL2 supports "punning" where the same IRI is used to identify both a class
;; and an individual. This is useful both to fit with the RDF representatation
;; of OWL, and also by providing a form of meta-class modelling. Semantically,
;; the two are kept separate -- when reasoning the link between the two is
;; ignored. It also creates a problem for Tawny where the string
;; representation of an entity (`Bill`, `Person` or `hasSSN` for instance)
;; maps to the IRI. We need to know whether we are talking about the
;; individual or the class. So, here, we create an individual directly using
;; the `individual` function rather the `defindividual` macro; we use the same
;; IRI as `Father`. Finally, we intern the object created into a var with name
;; `iFather`. In the Clojure space, as with the reasoner space, we keep these
;; two separate, although both will have the same IRI in OWL.
(def iFather
  (individual (.getIRI Father)
              :type SocialRole))

;; Jack is many things, but having children and being 53 are not two of them!
(defindividual Jack
  :type Person (not Parent)
  :fact (fact-not hasAge (literal 53)))

;; Although this is one of the longest definitions, it brings nothing new that
;; we have no used before.
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

;; Has value allows us to assert a relationship between a class and an
;; individual. In this case we assert that `JohnsChildren` are those
;; individuals whos parent is `John`.
(defclass JohnsChildren
  :equivalent (has-value hasParent John))

;; This is an example of the has-self relationship, which states that a
;; `NarcisiticPerson` is one who loves themselves.
(defclass NarcisticPerson
  :equivalent (has-self loves))

(defclass Woman
  :subclass Person)

(defclass Dead)
(defclass Orphan
  :equivalent (only (inverse hasChild) Dead))

;; We now move on to datatypes. In this case, we define a person an number
;; with a min of 0 and a (rather arbitrary) max of 150.
(defdatatype personAge
  :equivalent (min-max-inc 0 150))

;; The `span` macro provides a syntactic variant of `min-mix-inc` and other
;; related functions.
(defdatatype minorAge
  :equivalent (span >=< 0 18))

;; We can use `owl-and` and `owl-not` on datatypes, although they have a
;; different interpretation to the class based use. For this to work, Tawny
;; must be able to determine the type of `personAge`; this is straightforward
;; when using interned symbols as here. If we use the function forms and
;; strings, then it is a little harder.
(defdatatype majorAge
  :equivalent (owl-and personAge (owl-not minorAge)))

;; `toddlerAge` is defined as an enumeration of 1 and 2.
(defdatatype toddlerAge
  :equivalent (oneof 1 2))

;; Again, `oneof` is overloaded and can be used either with datatype
;; properties, data values (such as 1 and 2) or, as here, with individuals.
(defclass MyBirthdayGuests
  :equivalent (oneof Bill John Mary))

;; We define a teenage as having an age between 13 and 19. We could also have
;; defined `teenAge` as a datatype and then used it here.
(defclass Teenager
  :subclass (some hasAge (span >< 12 19)))

(defclass Female)
(defindividual Meg)


;; We define a (GCI)[http://ontogenesis.knowledgeblog.org/1288] or General
;; Concept Inclusion, which defines a general rule relating two different
;; class expressions. In this case, we make direct use of the `add-subclass`
;; form. We have considered allowing use of complex classes directly in as the
;; first argument to `owl-class` however, not all of the frames can be applied
;; to a GCI, most notable, annotations which can only be added to the subclass
;; axiom (using the pattern shown previously for `Man` and `Person`).
(add-subclass
 (and Parent
      (at-most 1 hasChild)
      (only hasChild Female))
 (and (oneof Mary, Bill, Meg) Female))

;; And finally, we add another acronym.
(as-equivalent
 (defclass Adult)
 other/Grownup)


;; # Conclusions
;;
;; This concludes the Tawny version of the OWL API.
