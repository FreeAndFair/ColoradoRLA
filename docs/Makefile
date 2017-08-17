# Build system for system documentation.

.PHONY: all clean

# Note that the prefix numerals on these filenames is used to leverage
# a lexicographical sort on them when calling pandoc below using a
# shell globbing character '*".

MD_DOCS := 01_book.md 05_license.md 10_team.md 15_installation.md \
           20_runbook.md 25_developer.md 30_project_management.md \
           35_methodology.md 40_v_and_v.md 45_security.md \
           50_requirements.md 55_specification.md 89_glossary.md \
           99_bibliography.md

all: book.pdf book.html runbook.docx user_manual.docx

book.pdf: $(MD_DOCS)
	pandoc *.md --toc --variable linkcolor -f markdown -t latex -o book.pdf

book.html: $(MD_DOCS)
	pandoc *.md --toc --variable linkcolor -f markdown -t html5 -o book.html

runbook.docx: 20_runbook.md
	pandoc 20_runbook.md --variable linkcolor -f markdown -t docx -o runbook.docx

user_manual.docx: 23_user_manual.md
	pandoc 23_user_manual.md --variable linkcolor -f markdown -t docx -o user_manual.docx

clean:
	rm -f book.pdf book.html runbook.docx user_manual.docx
