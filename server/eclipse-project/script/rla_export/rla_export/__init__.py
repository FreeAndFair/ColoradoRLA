"""rla_export: Export data from ColoradoRLA to allow public verification of the audit
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Examples
--------

With no options, the command will run queries using
all the standard .sql files provided in the package, and
put the resulting exported data in files in the current directory.

  ``rla_export``

The optional -p argument specifies connection information via
a database properties file, which should be the same file used
for the ``java jar`` command line. The output can also optionally be put
in a different output directory using the -e argument.

  ``rla_export [-p properties_file] [-e export_directory]``

Export a query in json and csv format for selected sql files:

  ``rla_export file.sql ...``

Full command line usage synopsis:

  ``rla_export -h``

See README.rst for documentation.
"""

from __main__ import main
