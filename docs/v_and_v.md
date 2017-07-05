Validation and Verification
===========================

Figuring out if the system you are creating is the system that a
client wants is called *validation*.  *Testing* is one means by which
to perform validation.  Proving, mathematically, that a system
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
in. This field of R&D is known as formal verification; many members of
our team are world leaders in this topic, having been professors and
professionals inventing and publishing new concepts, mathematics,
tools, and techniques in this area for decades.

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
data formats; this will help ensure that the tests accurately
represent the system requirements without being influenced by
implementation choices. These will be performed under live conditions.

The third level of dynamic checking is user interface (UI) testing. UI
testing is different, because it takes special tools to drive a UI for
a system in a way that is repeatable but still at the level of a human
user. There are two kinds of UI testing to consider. First, during UI
design, a prototype of the final system is used to simulate the user’s
experience of the actual system. We will use this prototype to get
early and regular feedback from users, both via interaction with the
system in Virtual Reality and via prototypes (we discuss this in more
detail below). This feedback will inform our iterative design
improvements. Additionally, testing sessions can be observed,
recorded, and shared with other team members to ensure that end user
experience remains central during the design process. We will invite
Travis County officials to both take part in and view these studies,
either directly or remotely. This will give Travis County a deeper
understanding of how the users of the system are driving our design
choices. During the design process, these will be performed by live
users on systems that are increasingly accurate mock-ups of the
eventual technology.

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

As User Centered Design (UCD) is incorporated through the discovery,
design, and build stages of the project, usability testing is woven
throughout system design and development.

Usability goals and assessments are grounded in a research phase at
the beginning of the project during which current and relevant
research, both academic and corporate, is explored, vetted, digested,
and shared by the team. Our UCD lead will spearhead the research and
create a findings report to share with the team at large, ensuring the
group has a shared understanding of relevant previous work. The UCD
lead will also act as a research liaison, helping guide other team
members to personally relevant materials to support their roles in the
project.

Since the audience must be broad and inclusive, our approach will be
to focus on high-needs cases and adopt an agile, iterative approach
with many rounds of lightweight user testing. During this phase, we
will ensure the designs are Section 508 compliant.

Our designers will start with lightweight design iteration and
incorporate user testing as early as possible. These early tests may
be done with simple paper prototypes to test out ideas. As the ideas
progress and refine, an interactive prototype is constructed. Our
usability testing is purely focused on prototype development. Feedback
and input provided by users shape the UI design concepts in the
context of the stated product requirements.

Overview of Usability and Accessibility Testing. Early user testing
will be done on a bi- weekly basis with 5 to 8 users recruited through
professional networks. We take our testing approach from user
experience experts in the Nielsen Norman Group, who wrote a seminal
article on the ideal number of participants and structure of user
testing sessions. They state that 5 to 8 participants yield almost all
of the meaningful findings. Additionally, they suggest that teams
interested in getting their design down to a very small margin of
error run repeated smaller testing sessions instead of few larger
testing sessions.

As the design matures, testing will continue and will culminate with
formal audience definitions. Our team will run one more user testing
session after the design process has finished. Feedback from the
testing will be incorporated and a final design will be released.

As such, usability and accessibility testing takes place in several
different phases of the project. UI prototypes witness usability
testing during design, even before an interactive demonstration is
complete. System prototypes witness usability testing in Virtual
Reality prior to any hardware being built. Interactive prototypes are
created for all UI components. Since UI specifications and prototypes
are a part of the static and dynamic specification of the productized
implementation of UI subsystems, once those subsystems are completed
and have a complete assurance case, they will not need usability or
accessibility testing prior to being submitted for certification at
our VSTL.

Observational research is collected throughout these studies and will
be integrated across the team (and potentially with the performer on
Element E) to include accessibility and inclusivity of all users
identified throughout the up-front planning and profiling of
users. Our design team uses a variety of methods including
storyboarding, concept testing, heuristic review, journey mapping, and
user frameworks for testing and validating concepts. Low-fidelity
prototyping, usability studies, and user interviews provide iterative
feedback that is incorporated into concepts that are fully baked and
provided as final design that meet the objectives of the County, as
detailed in the RFP and the earlier RFI. Usability testing can be
applied to many facets of product development from the early stages of
research through the final product launch.

Details of Accessibility Testing. Our team intends to do regular user
testing to benefit from iteration both for designers and for test
participants. Assuming the research and design phases last
approximately four months, we will test approximately six times on
roughly a bi-weekly basis.

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
during component integration. It is also part of the continuous
integration practice that is described in Section 4.8.

System Verification
-------------------

