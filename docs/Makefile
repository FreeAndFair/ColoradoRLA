# Build system for system documentation.

.PHONY: all clean

# Note that the prefix numerals on these filenames is used to leverage
# a lexicographical sort on them when calling pandoc below using a
# shell globbing character '*".

MD_DOCS := 01_book.md 05_license.md 15_installation.md 25_developer.md \
           30_project_management.md 35_methodology.md 40_v_and_v.md 45_security.md \
           50_requirements.md 55_specification.md 65_deployment.md 70_team.md \
           89_glossary.md 99_bibliography.md

all: book.pdf book.html county_runbook.docx sos_runbook.docx user_manual.docx export_manual.docx export_manual.html

book.pdf: $(MD_DOCS)
	pandoc $(MD_DOCS) --toc --variable linkcolor -f markdown -t latex -o book.pdf

book.html: $(MD_DOCS)
	pandoc $(MD_DOCS) --toc --variable linkcolor -f markdown -t html5 -o book.html

county_runbook.docx: 22_county_runbook.md
	pandoc 22_county_runbook.md --variable linkcolor --table-of-contents --reference-docx=reference_fandf_pages.docx -f markdown -t docx -o county_runbook.docx

sos_runbook.docx: 24_sos_runbook.md
	pandoc 24_sos_runbook.md --variable linkcolor --table-of-contents --reference-docx=reference_fandf_pages.docx -f markdown -t docx -o sos_runbook.docx 

user_manual.docx: 20_user_manual.md
	pandoc 20_user_manual.md 89_glossary.md --variable linkcolor --table-of-contents --reference-docx=reference_fandf_pages.docx -f markdown -t docx -o user_manual.docx

export_manual.docx: 26_export_manual.md
	pandoc 26_export_manual.md --variable linkcolor --table-of-contents --reference-docx=reference_fandf_pages.docx -f markdown -t docx -o export_manual.docx 

export_manual.html: 26_export_manual.md
	pandoc 26_export_manual.md --variable linkcolor --table-of-contents -f markdown -o export_manual.html

clean:
	rm -f book.pdf book.html runbook.docx county_runbook.docx sos_runbook.docx user_manual.docx
