<p align="center">
  <img width="768" height="247" src="resources/shar.svg?raw=true">
</p>

# SHAR — The Scala & HermiT Algebraic Representation

- [The SHAR API](#the-shar-api)
    - [Quick Start](#quick-start)
    - [SHAR in Depth](#shar-in-depth)
    - [Replacing HermiT](#replacing-hermit)
- [References and Examples](#references-and-examples)

*SHAR* features a collection of algebraic data types for description logic expressions and axioms in a robust API for working with the HermiT reasoner (as well as other OWL-API based reasoners), that includes additional tools such as parsers from two DL notations (formal Unicode-based as well as an ASCII-only variant).

## The SHAR API

The high-level *SHAR* API has two modes: Safe and unsafe, pertaining to how String-encoded information is handled. Essentially, any IRI, Prefix, and concept expression can be parsed safely, i.e., returning instances of `SharTry[Iri]`, `SharTry[Prefix]`, or `SharTry[Concept]`, respectively, where `SharTry[T]` is an alias for `Either[SharError, T]`; or unsafely, where any parse- or encoding error produces a `RuntimeException`. 

### Quick Start

Usually (and in all examples below) the only required import is:

```scala
import de.pseifer.shar._
```

In the following **safe-mode** example, we first define a custom prefix, instantiate an instance of `SHAR`, add some subsumption axioms, and check for entailment of another axiom. 

```scala
val result = for
  // Parse a prefix.
  p <- ":".toPrefix
  // Parse an IRI.
  i <- "<https://github.com/pseifer/shar/>".toIri

  // Initialize the backend with a prefix mapping.
  s = SHAR(PrefixMapping(p -> i))

  // Parse concept expressions.
  c1 <- s.parse(":Person") // ":Person".dl with 'import s.safe._'
  c2 <- s.parse(":Agent")
  c3 <- s.parse(":Person ⊔ :Agent") // Alternative: Union(c1, c2)

  // Add subsumption axioms to the default HermiT instance.
  _ = s.addAxioms(c1 <:< c2, c2 <:< c1)

  yield
    // Prove axioms using the default HermiT instance.
    s.prove(c3 =:= c1) // true
```

The unsafe variant is intended for trusted, source-code literals, interactive use, or simple scripts. Since the previous example relies entirely on plain literals in the source code (and not external strings, or an external ontology), we could also use the more convenient **unsafe-mode** to implement this example:

```scala
val s = SHAR(PrefixMapping(
    ":".prefix -> "<https://github.com/pseifer/shar/>".iri))
import s.unsafe.given

s.addAxioms(
    ":Person" <:< ":Agent", 
    ":Agent" <:< ":Person"
)
val result = s.prove(":Person" =:= ":Person ⊔ :Agent")
// > true
```

This is the briefest version, relying on the import of given conversions from `String` to `Concept`. There are also explicit `String` extension methods named `iri`, `prefix`, and `dl`, that can be used in cases where the context cannot be inferred. There are both safe and unsafe variants of both conversions and extensions. The safe versions are available via `import s.safe.given`. (*NB. Why imports from instances of SHAR?* Parsing relies on the `SHAR` configuration, e.g., the prefix definitions in scope.) Of course, both can be combined. We recommend using the unsafe `iri` and `prefix` extension methods for prefix and IRI definitions, and then working within `SharTry` for parsing and reasoning with concept expressions. This is because runtime errors related to `Prefix` and `Iri` for the construction of `SHAR` instances are usually observed directly and locally, whereas for concept expressions this might not always be the case.

Finally, there is also a `StringContext` for constructing concept expressions that supports splicing of any arbitrary `Concept` into the string encoded expression. There are explicit versions using `dl"".dl` (safe or unsafe), and implicit conversions in both `unsafe.given` and `safe.given` (on an instance of `SHAR`). The implicit version can be used as shown below.

```scala
// ...
import s.unsafe.given // or s.safe.given

val c1 = ":Person".dl
val c2: Concept = ":Agent"
val a = dl"${c1} ⊔ ${c2}" <:< c1
```

### SHAR In-Depth 

*SHAR* features a collection of abstract data types for representing DL concept expressions and axioms, and interacting with reasoners. The most convenient way to construct instances for concept expressions, is using the built-in parsers, as demonstrated in the Quick Start examples. To this end, *SHAR* supports the common DL syntax, utilizing Unicode symbols such as `⊔`, `∃`, or `⊤`. Alternatively, each symbol can be represented by one (or more) ASCII symbols. The table below lists the ADT type name, associated Unicode symbol, and ASCII symbol sequence. Here, `i` is a meta-variable for an IRI, `r` for a role name, and `c, d` for concept names, and `n` for an integer. An IRI can be specified surrounded by `<c>`, or using a prefix, as `prefix:c`. The grammar is also available [here](src/main/antlr4/DescriptionLogics.g4).

| Type                   | Unicode    | ASCII     |
|------------------------|------------|-----------|
|`Top`                   | **⊤**      | `#t`      |
|`Bottom`                | **⊥**      | `#f`      |
|`NamedConcept(i)`       | **i**      | `i`       |
| `NominalConcept(i)`    | **{i}**    | `{i}`     |
| `Complement(c)`        | **¬c**     | `!c`      |
| `Union(c, d)`          | **c ⊔ d**  | `c \| d`  |
| `Intersection(c, d)`   | **c ⊓ d**  | `c & d`   |
| `GreaterThan(n, r, c)` | **≥n r.c** | `#>n r.c` |
| `LessThan(n, r, c)`    | **≤n r.c** | `#<n r.c` |
| `Exactly(n, r, c)`     | **=n r.c** | `#=n r.c` |
| `Universal(r, c)`      | **∀r.n**   | `#A r.n`  |
| `Existential(r, c)`    | **∃r.n**   | `#E r.n`  |

Axioms can be created from `Concept` with the infix operators `<:<`, `=:=`, and `~` as alternatives for the constructors `Subsumption`, `Equality`, and `Satisfiability`. In the introductory examples, we used the implicit (lazily created) instance of the *HermiT* reasoner created for instances of `SHAR`. In general, reasoner instances are mutable and support adding of new axioms via `addAxioms`, and checking for entailment via `prove`. Reasoners can be instantiated with an ontology, by providing an IRI to their constructor; for the high-level `SHAR` API, the following example demonstrates this:

```scala
val s = SHAR(
    prefixes = PrefixMapping(
        ":".prefix -> 
          "<http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#>".iri),
    init = OntologyInitialization(
        "<https://www.w3.org/TR/owl-guide/wine.rdf>".iri
    ))
import s.unsafe.given

val r1 = s.prove(":RedWine" <:< ":Wine")
val r2 = s.prove(":RedWine" <:< ":WhiteWine")
```

In addition to the default *HermiT* instance, new instances can be created with `mkHermit`, which are configured using the `init` value on the `SHAR` instance, and take an optional `HermitConfiguration`, which wraps the internal configuration of the *HermiT* reasoner.

### Replacing HermiT

*SHAR* supports any OWL-API based reasoner. When using other reasoners (i.e., any reasoner created by a `OWLReasonerFactory`), such as *JFact* or *Openllet*, the generic `OwlApiReasoner` can be used. For example, to use *JFact* (after including the required dependency), a suitable instance can be created by passing its factory to `mkFromFactory`.

```scala
import uk.ac.manchester.cs.jfact.JFactFactory

val s = SHAR()
val r = s.mkFromFactory(JFactFactory())
r.prove(~Top)
```


Entirely custom reasoner implementations (i.e., not implementing `OWLReasonerFactory`) can also be supplied by instantiating the `DLReasoner` trait, directly. To this end, functions `addAxioms` and `prove` must be implemented. Instances can be created with `mkReasoner`.

```scala
import de.pseifer.shar.core._
import de.pseifer.shar.reasoning._

class FalseReasoner(init: ReasonerInitialization) 
    extends DLReasoner(init):
  def addAxioms(axioms: AxiomSet): Unit = ()
  def prove(axiom: Axiom): Boolean = false

val r = SHAR().mkReasoner(FalseReasoner(_))
```

## References and Examples

- [HermiT Reasoner](http://www.hermit-reasoner.com/)
- [Openllet Reasoner](https://github.com/Galigator/openllet)
- [JFact Reasoner](https://jfact.sourceforge.net/)
- [OWL API](https://github.com/owlcs/owlapi)
- [From Shapes to Shapes](https://github.com/softlang/s2s) uses *SHAR*
- DL REPL [SHARDIK](https://github.com/pseifer/shardik) uses *SHAR*
