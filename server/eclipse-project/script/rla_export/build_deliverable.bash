# Build a deliverable for rla_export

relname=rla_export-1.1.0
deliverable=deliverable-$relname
mkdir -p $deliverable

# These should be the same
diff rla_export/default.properties /srv/s/electionaudits/ColoradoRLA/server/eclipse-project/src/main/resources/us/freeandfair/corla/default.properties

# First run make in the docs directory
# and runsetup sdist in this directory

cp -p dist/$relname.tar.gz $deliverable
cp -p rla_export/corla.ini $deliverable/corla-template.ini
cp -p ../../../../docs/export_manual.docx $deliverable
cp -p ../../../../docs/export_manual.html $deliverable
pandoc -t html README.rst > $deliverable/README.html

zip -r $deliverable.zip $deliverable
