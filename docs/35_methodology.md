\pagebreak

Development Process and Methodology
===================================

We detail below our rigorous systems engineering and software
engineering process and methodology. We make objective decisions about
the appropriate programming languages, tools, and technologies for
each project or product. Our toolbox, especially in matters related to
rigorous systems engineering and applied formal methods, is broader
and deeper than that of any other company in the world.

History
-------

* Outline and first draft, 5 July 2017 by Joe Kiniry.

The Free & Fair Development Methodology
---------------------------------------

The specific development methodology we use for all of our software is
a variant of Design by Contract with some aspects of a Correctness by
Construction approach. Our process, method, tools and technologies
span several deployment and development platforms, specification and
programming languages, and communication and coordination schemes. In
short, we use a combination of the following methodologies:

* Correct-By-Construction
* Design-By-Contract
* Refinement-Based Process
* Kiniry-Zimmerman Methodology
* Business Object Methodology
* Formal Hardware/Software Co-Design
* Formal Methods (Alloy, CASL, Event B, RAISE, VDM, Z)

The design specified in the RFP includes security, fault tolerance for
robustness, and scalability. Our software development process
emphasizes those same qualities. This type of development process has
been used to develop hundreds of millions of dollars’ worth of
military, aviation, biomedical and financial systems that must not
fail, because human lives and billions of dollars hang in the balance.
We believe that election systems are just as important, because
protecting democracy protects human lives and our whole economic
system.

Our systems are all fault tolerant and have sufficient
redundancy, both in algorithm design and physical architecture, to
ensure that they can survive the simultaneous failure of multiple
machines or networks.

We will apply the same high-assurance techniques to ensure that this
project is developed not only with generic coding best practices, but
also with best practices for systems critical to homeland security and
medical applications. The system will be far less prone to failure
than even the best standard office software.

One important coding best practice for critical systems is performing
a machine-checked functional verification of the core algorithms of
the software. We first design a mathematical model that is as easily
understood as the English language specification. We then provide an
implementation that is mathematically proven to meet the
specification. This mathematical proof can be automatically checked on
a computer, giving unparalleled assurance that the software is
correct. These techniques have historically been used for
safety-critical systems, where the failure of a system would result in
loss of life (e.g., flight control systems at Airbus) or have enormous
cost implications (e.g., failure of a mission to Mars).

By combining these techniques, we create a chain of correctness that
starts with the high-level system specification and traces down to the
smallest implementation details of the most critical parts of the
system. At each step in the chain we focus on providing evidence of
correctness, generally in multiple forms, including refinement proofs
from informal to formal specifications, unit test suites, and
mathematical proofs of correctness and security. In other words, all
the effort we put into ensuring that our system is correct generates
tangible evidence that gives external parties (e.g. certification
labs, security experts, political parties, and the American public)
the same confidence in our software that we have.

We strictly adhere to well-documented code standards for the various
programming languages in which we develop software, and we use
appropriate development and testing environments (IDEs, continuous
integration systems, issue trackers) to support our development
efforts. We aggressively employ techniques such as linting (automated
syntactic checks to catch early programming errors), static analysis,
and automated testing to provide continuous feedback on our software
development practices.

Our formal domain models provide a high-level view of the logical and
modular design of the system, ensuring robustness and
scalability. From this view, it is easy to see how the components
communicate and what their interdependencies are. It is easy to detect
components that are too tightly coupled, which would make future
replacement or revision challenging; a loosely-coupled system allows
for easy extensibility. Our domain models also make it clear what
interfaces need to be satisfied by any future module replacement. This
means that there is no danger of swapping out a module for a
replacement that does not have all the expected functionality. Once
the domain model is satisfactory, we continuously use analysis tools
to guarantee that all code we write conforms to the model.

We also create formal models of every data format that we use, both
internally and externally. We can use these formal models to generate
software that allows a variety of programming languages to communicate
natively via our data formats, providing fluent interoperability.

Our designs are always highly modular. Each module uses only open data
formats for communication, resulting in a system that can easily
integrate with third party systems and can be modified and upgraded by
anyone who is familiar with the data formats. A modular architecture
assists with validation and verification, allows for experimentation
with user experience variants, and enables phased user acceptance
testing. It can also ease customization of the system, allowing new
voting methods and audit protocols to be swapped into the system as
needed without requiring system-wide changes. 

Our development repositories contain the code under development, the
full set of development artifacts described above, and unit,
performance, and integrated functional test suites for each subsystem
and for the system as a whole. Our test servers pull from these
repositories and perform automated builds and testing whenever code is
updated. In addition to standard functional tests we place a
particular emphasis on performance tests, which allow us to ensure
that feature changes do not impact performance.

Example Use of Our Methodology
------------------------------

Historically, this kind of system has been evaluated in an ad hoc
manner based upon informal requirements documents, a repository of
source code, a User’s Manual, and some examples of its use. By
contrast, our rigorous system design and assurance tests are derived
systematically from the client requirements. 

Our rigorous systems development method for the aforementioned Dutch
election system produced three particularly useful artifacts: (1) a
formal specification of the domain model of Dutch elections, (2) a
formal architecture description, and (3) a model-based
design-by-contract specification of the system.

Producing item (1) revealed over one hundred errors in Dutch election
law and the election system that we were asked to integrate
with. Catching these errors engendered confidence that the system we
created fulfilled the requirements stipulated in law.

Item (2) let the team decompose the development work into three
strictly separated subsystems (UI, I/O, and core data types and
algorithms), implement and verify those subsystems completely apart
from each other (this is called compositional verification), and plug
them together at the end of the project, resulting in a system that
operated correctly the very first time it was executed. This
decoupling permitted the team to parallelize work, avoiding
inefficiencies related to inappropriate relationships between
subsystems, and let us ensure that the implementation of the system
conformed to its architecture, avoiding what is known as architecture
drift.

Item (3) helped achieve greater assurance faster than any traditional
engineering approach. In particular, our methodology enabled the team
to automatically generate unit, subsystem, and integration tests from
model-based specifications, saving an enormous amount of time over
hand-written tests. Additionally, we provided assurance about the
consistency and coverage of those tests against election law and
client requirements because we were able to trace tests all the way
from law to code. Finally, we used those specifications to formally
verify that the implementation behaved correctly under all possible
inputs. We performed this verification using an advanced static
analysis technique known as extended static checking, a technology for
which Dr. Kiniry and other team members are internationally
recognized.

Continuous Validation, Verification, Integration, and Deployment
----------------------------------------------------------------

Development follows a continuous validation, verification,
integration, and deployment approach. Each code change is assessed
automatically, as soon as it can be, to catch defects as early as
possible. Continuous integration is a proven way to reduce development
cost and improve productivity. In continuous deployment, code that has
passed validation and verification ("V&V") and integration testing is
promoted automatically from the main development branch to a
deployment staging area that allows all project personnel to access
and test the latest working code in a whole-system context.

We will use the external build tool Travis CI, which is configurable and 
is free of charge to open source projects.

Documentation and commenting is interwoven with development. Beginning
with our formal domain models, we lay out the requirements and
expectations of each module. We do this both formally (in a language
that we can automatically check later) and in plain English. Moving
forward, we translate both of these specifications into appropriate
constructs (documentation comments, assertions, annotations, type
specifications) in our programming language of choice. This enables
the automatic generation of PDF and HTML documentation describing each
piece of the software and showing relevant code snippets.

We use Git for version control within GitHub and leverage commit hooks
(for automatically running testing tools, static checking tools,
etc. every time the codebase is changed) and issue tracking. Many of
the tools we enumerate in the [Developer Instruction](developer.md)
documentation have built-in sharing and version control
capabilities. We leverage those capabilities and have snapshots of
design artifacts captured in our distributed version control system
like any other engineering artifact.

When Free & Fair notices a defect, we will log the issue immediately
into the issue tracking system within GitHub. If the Colorado
Department of State would like direct access to GitHub to log issue,
we will provide access; the Colorado Department of State may prefer to
notify Free & Fair by email so that Free & Fair can enter the issue
into the tracking system.  Within a day of entry to the system, each
issue will be categorized and assigned to a team member who will be
responsible for driving the effort to fix the issue. Each code change
that has an effect on an issue will reference that issue, so that
progress towards a fix can be observed as it occurs. We will maintain
a policy that an issue can be closed only once the party that raises
the issue signs off that the issue has, in fact, been fixed.

All development-related team communication is facilitated by an Asana
project and several Slack channels that are accessible to and editable
by all team members. Specific Slack channels also serve as the
reporting facility for team metrics and the home of software
documentation during development activities. We use metrics such as
test suite success rate and defect escape rate to monitor code health,
adapting our test suites and design review processes to meet goal
lines for each metric.

If Colorado wishes to contract us for ongoing upgrades to the system,
we will use Ansible or a similar configuration management tool to
ensure that environments remain consistent across machines. Note that,
since configuration management exists for the convenience of the
client, we are comfortable with any variety of configuration
management tools (such as CFEngine, Puppet, Chef, etc., as appropriate
for the given programming language and deployment platform) and will
work with CDOS department to come to a suitable solution.

Finally, we can also package and deliver full snapshots of development
environments in virtual machine images. We typically use VirtualBox
for such work.

