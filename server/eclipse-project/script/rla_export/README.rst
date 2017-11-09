Documentation for ``rla_export``
================================

The ``rla_export`` command exports ColoradoRLA data for publication
on the Audit Center web site.

Installation
------------

If some version of Python's package manager pip is already installed, skip to the package installation pip command. If not, you will have to install pip. The pip installation depends on the operating system and version.

For RHEL 7, as a one-time step on each RLA Tool server, run these commands to install
"pip" command for installing packages and the necessary dependencies.

::

    yum install epel-release
    curl https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm > epel-release-latest-7.noarch.rpm
    yum install ./epel-release-latest-*.noarch.rpm
    yum install python-pip
    sudo pip install --upgrade pip


Once pip is installed, you can use it to install rla_export and all the packages it depends on, by
* running a command like this on the file containing the rla_export package (the ``*.tar.gz`` file provided).
  Note that the version number may be different from ``1.0.4.dev2`` and you'll need to specify a
  properly qualified path name if it isn't in the current directory.

  ``pip install rla_export-1.0.4.dev2.tar.gz``

Running the Export Command
--------------------------

With no options, the command will run queries using
all the standard ``.sql`` files provided in the package, and
put the resulting exported data in files in the current directory.

It will also download any reports that are available, and a csv-format
list of ballot cards for auditing for all rounds defined to date.

  ``rla_export``

If you are using a customized database properties file on the ``java jar``
command line, you should provide that same file with the ``-p`` option,
so that the program knows the proper connection and login information for the database:

  ``rla_export -p properties_file``

The output can also optionally be put in a different output directory,
which will be created if necessary,
using a ``-e export_directory`` argument.

If you wish to export the results of a custom database query in json and csv format, put the query into ``file.sql`` and add the file name as an argument:

  ``rla_export file.sql``

The export from this command will have just a json and a csv file for the single SQL command in ``file.sql``.

Full command line usage synopsis:

  ``rla_export -h``

Exported files
--------------

Some export files are the same as the files available via the GUI interface,
for example ``state_report.xlsx``.

Other export files are generated based on ``sql`` query files.
These are always produced in two formats: json and csv.
The basename of each resulting file is the same as the basename of the query file.
Thus, given the query file ``seed.sql``, the files ``seed.json`` and ``seed.csv``
will be produced.

Detailed documentation of individual tables is available in the [docs folder](https://github.com/FreeAndFair/ColoradoRLA/tree/master/docs).