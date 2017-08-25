Eclipse Installation
--------------------

The installation of the various plug-ins we use for development,
code quality, etc., is straightforward, but is nowhere near as automated
as we would like. To install an Eclipse distribution with all the required
tools and get the project ready for development, do the following:

  1. Download the Eclipse Oxygen installer for your platform from
  http://www.eclipse.org/oxygen/.
  2. Run the installer and install the "Eclipse IDE for Java Developers"
  (the first option on the list of distributions to install).
  You may encounter popup messages about nullpointer exceptions and
  event loop exceptions, but re-running things should get you going again.
  On Ubuntu you may want to use the instructions at
  [How to install Eclipse using its installer - Ask Ubuntu](https://askubuntu.com/questions/695382/how-to-install-eclipse-using-its-installer).
  Select OpenJDK 8 VM as the Java VM, if you have it.
  Otherwise, Oracle Java 8 should work.
  3. Run the resulting Eclipse installation (the installer may do this
  automatically for you) and create a workspace, *which you can use for
  development of this project and others too*, somewhere outside your
  clone of the Git repository. If you have not already cloned the Git
  repository, you can do so later, and Eclipse can help you.
  4. Use "Import..." (in Eclipse's "File" menu or the context menu of the
  Package Explorer) to import an Oomph project into the workspace.
  This will result in a window with a list of Eclipse products and projects.
  5. Click the "+" button (upper-right), which will bring up a window with
  "Catalog" and "Resource URIs"; select the Eclipse Projects catalog
  and put the URI
  https://raw.githubusercontent.com/FreeAndFair/ColoradoRLA/master/server/eclipse.setup
  into the text field at the bottom, then click "OK".
  6. You should now have a "Free & Fair Colorado..." item in the projects
  list. Check it, and click "Next >"; then click "Next >" again, and
  finally "Finish".
  7. Wait. Eventually you will get a dialog asking you to accept or
  decline the installation of unsigned content, which you should accept.
  8. Wait some more. Eventually, there will be a flashing icon in the
  bottom of your Eclipse window that looks like a caution sign above a
  pair of arrows. Click it, then click "Finish". Your Eclipse will restart;
  pick the same workspace you started with.
  10. Add the ColoradoRLA project by using "Import..." (from "File" or
  context menu) to import "Existing Projects into Workspace" (under
  "General"). Eclipse will ask you to select a root directory; select the
  top level of your Git repository clone and you should see the
  "ColoradoRLA" project appear. Then, click "Finish". If you have not yet
  cloned the repository, you can either do so first, or import "Projects
  from Git" instead and let Eclipse clone it for you. Note that the
  Eclipse project will be linked, rather than copied, into your workspace.
  11. You are now ready to work. You can explore the project via 
  "`Window` -> `Show View` -> `Project Explorer`", and optionally open the AutoGrader
  view (by selecting it from the set presented by "Window -> Show View ->
  Other..." or searching Eclipse for "AutoGrader") to get continuous
  feedback on the project's code quality.

Maven Installation and Use
--------------------------

The server can be built and packaged
using [Maven](https://maven.apache.org/).

To use Maven, one must install it and
an [OpenJDK 8 Java Developers Kit](http://openjdk.java.net/).

From the `server/eclipse-project` directory, run the command `mvn
package` to compile the server, run the various static checkers we
have configured on the system, and package a distribution Java
archive.  You will find the latter in `server/eclipse-project/target`
with a filename akin to `colorado_rla-VERSION-shared.jar`.

