\pagebreak

Validation and Verification
===========================

Determining whether the system you are creating is the system that a
client wants is called _validation_.  _Testing_ is one means by which
to perform validation.  Mathematically proving that a system
performs exactly as specified under an explicitly stated set of
assumptions is called _verification_.

We perform both validation and verification on a regular basis, every
night when nightly builds are created and on every merge of new or
corrected functionality into the release branch of the development
repository.  This is standard development practice and minimizes
problems that can occur during component integration. It is also part
of our continuous integration practice.  We discuss our rigorous
systems engineering method in greater detail in
the [Development Process and Methodology](methodology.md) document.

Testing and Proving
-------------------

Testing provides some degree of assurance that a system will behave
according to its requirements. In mainstream software engineering,
test-driven development, often couched in agile processes, is in vogue
and considered a best practice. While we realize testing is important,
we do not use testing in the same fashion as other R&D
organizations. We are different because, as discussed elsewhere at
length, we use a rigorous systems engineering methodology based upon
applied formal methods.

Essentially, because we reason about programs and their
specifications, rather than hand-writing and hand-maintaining tests
that only describe a small fraction of a system’s functionality, we
formally describe how a system is meant to behave and _prove_
(formally, mathematically, mechanically) that the system will always
behave that way under all conditions. These correctness proofs give as
much assurance as testing every possible state of the system. This
field of R&D is known as formal verification; our team includes world
leaders in this topic, who have previously been professors and
professionals inventing and publishing new concepts, mathematics,
tools, and techniques in this area for decades.

Many properties a system should have cannot be tested; security is one
of the most noteworthy of these. Certainly, one can search the system
for known bad practices or "gotchas" (this is often viewed as
"security testing" in mainstream software engineering), but avoiding
all of the _known_ mistakes one can make says nothing about all of the
possible _unknown_ mistakes that can introduce security failures in
systems.

Despite the level of assurance we can achieve through formal
verification, there is still much to be learned from executing a
system and examining its behavior under execution, whether in a
virtual environment (e.g., virtualization) or in a physical one
(across different CPUs, operating systems, etc.). Below we explain the
means by which we test, and how that testing complements formal
verification.

System Validation
-----------------

Free & Fair will ensure that the delivered system meets its intended
security, performance, and accuracy (correctness) requirements by
applying dynamic checking (classic testing) at three levels: _unit
testing_, _functional testing_, and _user interface (UI) testing_.

Unit testing exercises each basic software component of the system
to ensure it meets its specification. The specification is obtained as
part of the refinement of the high-level system requirements through
the design process. Using our tools, most of these tests will be
generated automatically. Unit tests provide a low-level indication
that the basic components of a system are working as intended and
provide an early warning if changes cause requirements and
implementation to diverge.

Functional testing exercises the overall functionality of the
system. A range of use scenarios is designed to cover the full
requirements of the system, including even unlikely combinations of
input data. Functional tests can be designed without a full
implementation in hand, based on the system requirements and the input
data formats; this helps to ensure that the tests accurately represent
the system requirements without being influenced by implementation
choices. 

There are two types of user interface (UI) testing:
_usability/accessibility testing_ and _UI functional
testing_. Usability and accessibility testing will be handled by the
Colorado Department of State (CDOS) for this project, in accordance
with their processes for such; however, before delivering a version of
the system to CDOS for testing, we will run our own set of UI
functional tests to ensure that the implemented UI conforms to the
design and the product requirements built into the design. UI
functional testing is automated using tools that can drive the UI
based on test scenarios; the testing is run regularly to be sure that
no inadvertent code changes cause the implementation to diverge from
the design.

A part of the regular reporting during the project will be the status
of test suite development and test suite success, across all of the
kinds of dynamic testing described above. Since it is typically not
possible to test all possible scenarios, dynamic checking only
provides indicative evidence that a system performs as intended;
therefore, we will not only report test suite success information, but
also indicate what fraction of system behaviors our test suite covers.

System Verification
-------------------

In addition to system validation through dynamic checking, described
above, Free & Fair will use static (formal) verification to prove that
key components implement the system requirements. In contrast to
dynamic checking, static verification is performed without executing
the software, and provides information about the software's behavior
that is independent of particular test scenarios.

To perform static verification, the software implementation and
specification are each translated into an equivalent logical
representation and automated proof tools are used to ensure that the
implementation satisfies the specification. This is the formal variant
of testing processes that are known by many names, such as
component-based, component-level, and subsystem-level testing. Because
our specification and reasoning methods are compositional—we can
perform verification on each individual element of the software
independently of the others—these techniques also subsume what is
normally known as integration testing. 

A part of regular reporting during the project will be the status of
static verification, including information about exactly what
functionality has been statically verified.

