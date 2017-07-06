Validation and Verification
===========================

Determining whether the system you are creating is the system that a
client wants is called *validation*.  *Testing* is one means by which
to perform validation.  Mathematically proving that a system
performs exactly as specified under an explicitly stated set of
assumptions is called *verification*.

Testing
-------

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
formally describe what a system is meant to do and prove (formally,
mathematically, mechanically) that the system will always behave that
way under all conditions. These correctness proofs give as much
assurance as testing every possible state the system can ever be
in. This field of R&D is known as formal verification; our team
includes world leaders in this topic, who have previously been
professors and professionals inventing and publishing new concepts,
mathematics, tools, and techniques in this area for decades.

Many properties a system should have are, in fact,
untestable. Security is one of the most noteworthy. Certainly, one can
look for known bad practices or “gotchas”—what is often viewed as
security testing in mainstream software engineering—but avoiding all
of the known mistakes one can make says nothing about all of the
possible unknown mistakes that can introduce security failures in
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
applying dynamic and static checking at multiple levels.

Dynamic checking (classic testing) is applied at three levels. The
first is unit testing. At this level, each basic software component of
a system is tested to ensure it meets its specification. The
specification is obtained as part of the refinement of the high-level
system requirements through the design process. Using our tools, most
of these tests will be generated automatically. Unit tests provide a
low-level indication that the basic components of a system are working
as intended and provide an early warning if changes cause requirements
and implementation to diverge. These will be performed under live
conditions.

The second level of dynamic checking is functional testing—testing
that the overall functionality of the system is correct (e.g., that it
counts ballots correctly). For these tests a range of use scenarios is
designed, with test scenarios including even unlikely combinations of
input data. These tests are designed to cover the full requirements of
the system. Our functional tests can be designed without a full
implementation in hand, based on the system requirements and the input
data formats; this helps to ensure that the tests accurately
represent the system requirements without being influenced by
implementation choices. These will be performed under live conditions.

The third level of dynamic checking is user interface (UI) testing. UI
testing is different, because it takes special tools to drive a UI for
a system in a way that is repeatable but still at the level of a human
user. There are two kinds of UI testing to consider. First, during UI
design, a prototype of the final system is used to simulate the user’s
experience of the actual system. We will use this prototype to get
early and regular feedback from users. This feedback will inform our
iterative design improvements. Additionally, testing sessions can be
observed, recorded, and shared with other team members to ensure that
end user experience remains central during the design process.

Second, there is testing to be sure that the implemented UI conforms
to the design and the product requirements built into the design. This
kind of testing is automated using tools that can drive the UI based
on test scenarios; the testing is run regularly to be sure that no
inadvertent code changes cause the implementation to diverge from the
design. Since formal methods for verifying UI functionality and
responsiveness are less mature than for algorithmic verification, UI
testing is an important component of the overall assurance case.

Dynamic checking only provides indicative evidence that a system
performs as intended, because it is typically not possible to test all
possible scenarios. With this in mind, we will not only report test
success, but also indicate what fraction of system behaviors we cover
using dynamic checking.

In addition to dynamic checking, Free & Fair will use static (formal)
verification to prove that key components implement the system
requirements. In static verification, implementation and specification
are each translated into an equivalent logical representation and
automated proof tools are used to ensure that the implementation
satisfies the specification. These tests are performed directly on the
software and do not have a physical component. This kind of
verification is the formal variant of what is known by many names,
such as component-based, component-level, subsystem-level
testing. Because our specification and reasoning methods are
compositional, these techniques also subsume what is normally known as
integration testing. These formal checks are also automated and can be
replayed regularly to be sure that requirements and implementation
stay consistent as the project progresses. We discuss our rigorous
systems engineering method in greater detail in the [Development
Process and Methodology](methodology.md) document.

Security Testing

In addition to correctness, security is critical to election
systems. Correctness guarantees state that, if used as anticipated,
the system will give a correct result. Security guarantees state that,
if the system is used in an unanticipated way, it cannot give
misleading results. Assurance that nothing bad can happen cannot be
provided by dynamic testing; it can only be provided by formal
proof. Proofs of security guarantees will be among the assurance
artifacts and arguments that Free & Fair produces in support of the
delivered Elements. These tests are performed directly on the software
and do not have a physical component.

Usability and Accessibility Testing

We will follow best practices for user-centered design in this
project, and will design the system for accessibility.  The actual
usability and accessibility testing for this project will be performed
by the Colorado Department of State using the processes they have put
in place for such testing.


Final System Testing

The final type of testing is the testing of the hardware/software
combination as it would be delivered. For this purpose, a set of
acceptance tests is designed in cooperation with the client. The
acceptance tests are made as automated as possible, but may also
include manual tests. These tests are performed live.

One part of the regular reporting during the project will be the
status of test suite development and test suite success, across all of
the kinds of dynamic and static tests described above.

A final aspect of both dynamic and static checking is regular
rerunning of tests. Typically test suites are executed (and static
checks are re-run) every night and on every merge of new or corrected
functionality into the release branch of software development. This is
standard development practice and minimizes problems that can occur
during component integration. It is also part of our continuous
integration practice.

System Verification
-------------------
