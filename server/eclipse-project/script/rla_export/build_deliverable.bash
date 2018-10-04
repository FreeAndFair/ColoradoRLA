# Build a deliverable for rla_export

# This should match the value in setup.py
relname=rla_export-2.0.1
deliverable=deliverable-$relname
mkdir -p $deliverable

# These should be the same
diff rla_export/default.properties /srv/s/electionaudits/ColoradoRLA/server/eclipse-project/src/main/resources/us/freeandfair/corla/default.properties

# First, manually, run make in the doc directory
# and runsetup.bash sdist in this directory

cp -p dist/$relname.tar.gz $deliverable
cp -p rla_export/corla.ini $deliverable/corla-template.ini
cp -p ../../../../docs/export_manual.docx $deliverable
cp -p ../../../../docs/export_manual.html $deliverable
pandoc -t html README.rst > $deliverable/README.html

zip -r $deliverable.zip $deliverable
