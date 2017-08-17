# Build system for ASM documentation.

.PHONY: all clean

ASM_DOCS := Audit_Board_Dashboard_ASM.pdf County_Dashboard_ASM.pdf \
            DOS_Dashboard_ASM.pdf RLA_Tool_ASM.pdf

all: README.pdf README.html README.docx

README.pdf: $(ASM_DOCS) README.md
	pandoc README.md --variable linkcolor -f markdown -t latex -o README.pdf

README.html: $(ASM_DOCS) README.md
	pandoc README.md --variable linkcolor -f markdown -t html5 -o README.html

README.docx: $(ASM_DOCS) README.md
	pandoc README.md --variable linkcolor -f markdown -t docx -o README.docx

clean:
	rm -f README.pdf README.html README.docx
