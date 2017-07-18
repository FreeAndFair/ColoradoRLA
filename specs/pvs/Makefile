# Make a LaTeX document and accompanying PDF from the PVS specification

pvsfile = 	corla
generated_tex =	pvs-corla
document =	corla_model

REPO=../..
DELIT=$(REPO)/tools/delit.pl

all:	${document}.pdf

clean:
	rm ${generated_tex}.tex *.aux *.fdb_latexmk *.fls *.log *.out *.pdf
	
${generated_tex}.tex:	${pvsfile}.pvs
	$(DELIT) -f ${pvsfile}.pvs
	
${document}.pdf:	${generated_tex}.tex
	latexmk -pdf ${document}
