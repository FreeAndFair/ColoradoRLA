# Build system for system documentation.

MD_DOCS := 01_book.md 05_license.md 10_team.md 15_installation.md \
           20_tutorial.md 25_developer.md 30_project_management.md \
           35_methodology.md 40_v_and_v.md 45_security.md \
           50_requirements.md 55_specification.md 89_glossary.md \
           99_bibliography.md

all: book.pdf book.html

book.pdf: $(MD_DOCS)
	pandoc *.md --toc -f markdown -t latex -o book.pdf

book.html:
	pandoc *.md --toc -f markdown -t html5 -o book.html

clean:
	rm -f book.pdf book.html
