Eclipse Installation
--------------------

The installation of the various plug-ins we use for development,
code quality, etc., is relatively automatic (though not yet as automatic as
we would like). To install an Eclipse distribution with all the required
tools and get the project ready for development, do the following:

  1. Download the Eclipse Oxygen installer for your platform from
  http://www.eclipse.org/oxygen/.
  2. Run the installer and install the "Eclipse IDE for Java Developers"
  (the first option on the list of distributions to install).
  3. Run the resulting Eclipse installation (the installer may do this
  automatically for you) and create a workspace somewhere outside your
  clone of the Git repository (if you have not already cloned the Git
  repository, you'll have to do so later).
  4. Use "Import..." (in Eclipse's "File" menu or the context menu of the Package 
  Explorer) to import an Oomph project into the workspace. This will result in a window 
  with a list of Eclipse products and projects.
  5. Using the "+" button (upper-right), add the Colorado RLA setup file.
  The button will bring up a window with "Catalog" and "Resource URIs"
  options; select the Eclipse Projects catalog and put the URI
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
  pick the same workspace you started with (it will be the default).
  10. Add the ColoradoRLA project by using "Import..." (from "File" or context menu)
  to import "Existing Projects into Workspace" (under "General"). Eclipse will ask you
  to select a root directory; select the top level of your Git repository clone and
  you should see the "ColoradoRLA" project appear. Then, click "Finish". If you have
  not yet cloned the repository, you can either do so first, or import "Projects from 
  Git" instead and let Eclipse clone it for you. 
  11. You are now ready to work. You can optionally open the AutoGrader
  view (by selecting it from the set presented by "Window -> Show View ->
  Other..." or searching Eclipse for "AutoGrader") to get continuous feedback on the 
  project's code quality.
