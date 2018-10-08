Documentation for ``rla_export``
================================

The ``rla_export`` command exports ColoradoRLA data for publication
on the Audit Center web site, and for other monitoring and diagnostic
purposes.

Install the pip packagemanager
------------------------------

If some version of Python's package manager pip is already installed, skip to next section.

Pip installation depends on the operating system and version.

For RHEL 7, as a one-time step on each RLA Tool server, run these commands to install the
"pip" command for installing packages and the necessary dependencies.

::

    yum install epel-release
    curl https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm > epel-release-latest-7.noarch.rpm
    yum install ./epel-release-latest-*.noarch.rpm
    yum install python-pip
    sudo pip install --upgrade pip


Install rla_export
------------------

Once pip is installed, you use it to install rla_export and all the packages it depends on.
Locate the ``rla_export*.tar.gz`` file provided in the deliverable, and
run a command like this on it, substituting the proper version number.  You'll need to specify a
properly qualified path name if it isn't in the current directory.

  ``sudo pip install rla_export-2.x.y.tar.gz --upgrade``

Setup procedure for rla_export
------------------------------

Make a copy of the supplied ``corla-template.ini`` file and modify it, following
the directions in that file.

In it you should specify the path to the properties file for database configration,
as well as connection and authentication information for your application server.

If you are using DatabaseAuthentication, you can specify both a state administrator user and password,
and put any value in for the three grid values so all the authentication prompts will be skipped.

You use the ``-C`` option of rla_export to specify your configuration file.

Running the Export Command
--------------------------

By default, the command will run queries using
all the standard ``.sql`` files provided in the package, and
put the resulting exported data in files in the current directory.

  ``rla_export -C myconfig``

The output can also optionally be put in a different output directory,
which will be created if necessary,
using a ``-e export_directory`` argument.

  ``rla_export -C myconfig -e /tmp/new-exports``

If the ``-r`` (``--reports``) option is used, it will also download any reports
that are available, and a csv-format list of ballot cards for auditing for all
rounds defined to date.

If you wish to export the results of a custom database query in json and csv format,
put the query into ``file.sql`` and add the file name as an argument:

  ``rla_export file.sql``

The export from this command will have just a json and a csv file for the single SQL command in ``file.sql``.

To get a full command line usage synopsis, run:

  ``rla_export -h``

Recommended rla_export usage:
-----------------------------

When the counties have uploaded CVR and manifest files,
run ``rla_export`` with the ``-f`` option to capture all file uploads to date. They don't change
after the audit is started, so it isn't necessary to use the ``-f`` option later on.

For example:

  ``rla_export -C myconfig.ini -f -e /srv/full-upload-archive``

Run `rla_export` without the ``-f`` or ``-r`` options at any time to export just the database queries.

If you are running without access to the database, you can use the ``--no-db-query`` option to turn
those queries off, and the -r and/or -f options to export files from the application server.

For example:

  ``rla_export -C myconfig.ini -r --no-db-query -e $HOME/reports-export``


Exported files
--------------

Some export files are the same as the files available via the GUI interface,
for example ``state_report.xlsx``.

Other export files are generated based on ``sql`` query files.
These are always produced in two formats: json and csv.
The basename of each resulting file is the same as the basename of the query file.
Thus, given the query file ``seed.sql``, the files ``seed.json`` and ``seed.csv``
will be produced.

Refer to the supplied Export Manual for documentation on the exported data files and columns.
