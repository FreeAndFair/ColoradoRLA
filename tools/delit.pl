#! /usr/bin/perl -w
#
# Delit.pl
# Copyright (C) 2004 by Adriaan de Groot, University of Nijmegen
#
# Deduce a TeX version of a PVS file that is annotated with literate
# PVS tags. The TeX file is sent to stdout.
#
###

###
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
# 
#  THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND
#  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
#  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
#  ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
#  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
#  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
#  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
#  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
#  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
#  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
#  SUCH DAMAGE.
# 
###

###
#
# What is "Literate PVS"? It's a standard for including particular
# comments in a PVS file so that a literate PVS processor (LPP) can translate
# the PVS code plus comments into useful LaTeX files. In particular,
# the LPP can:
# 
# * Extract individual bits of PVS code - a single lemma, say - to
#   a file suitable for inclusion in a LaTeX document. This is useful
#   when you want to refer to such bits of code in an article
#   and don't feel like re-typing the code. (Called block mode)
# * Translate an entire PVS file to LaTeX code, setting the comments
#   as regular text between the PVS code. (Called file mode)
# * Strip all comments from a PVS file and produce LaTeX. (Called strip mode)
# * [new feature] Generating an index of LEMMAs and other pspecially marked
#   definitions. This can be combined with the complete translation feature,
#  above.
#
# Literate PVS just adds comments to your PVS file. Those comments direct
# the actions of the LPP. All literate comments start with %+ .
#
# Literate PVS syntax: Literate PVS uses comments for all processing
# instructions. There are three kinds of comments that affect when and
# where text is output:
# 
# * Six percent signs (yes, %%%%%%) are meta-comments and are deleted
#   from the output in all processing modes. I Typically use these
#   comments for the copyright header at the top of the file and to
#   set off blocks of PVS code from each other.
# 
# * Three percent signs (%%%) are output as regular text in the file
#   mode. The text after the percent signs can contain whatever LaTeX
#   commands you like -- it's output as-is. In block mode, three percent
#   sign comments are deleted from the output.
#
# * One percent sign (%) is output as regular text in block mode and deleted
#   in file mode.
#
# In addition, comments that start with %+ are processing instructions for
# the LPP.
#
###

###
#
# LPP commands for block and file mode.
#
# %+BEGIN <blockname>
# %+END <blockname>
#   These two comments control the LPP in block mode. In block mode, all
#   PVS code between the BEGIN and END is written to a _separate_ LaTeX
#   file named pvs-<blockname>.tex. If you use the same <blockname> more
#   than once in a PVS file, all the blocks are written together to the
#   output file. This lets you (re)group your PVS code to reflect your
#   presentation instead of the order it is in the PVS file in.
#
# %+IGNORE
# %+RESUME
#   These comments direct the LPP in file mode. All text between the
#   IGNORE and RESUME is ignored, and not printed to the output file.
#   If index mode is on, then LEMMA and INDEX entries _are_ indexed
#   even in IGNORE blocks. (For index mode, see below)
#
# You can mix and match BEGIN/END and IGNORE/RESUME blocks however
# you wish, because the comments apply to different modes of running
# the LPP.
#
# For example, the following file:
#
#	%%%%%% MyPVS.pvs
#	%%% This is a long and tedious PVS file.
#	%+BEGIN rat
#	%%% A type of rodent.
#	rat : TYPE+
#	%+IGNORE
#	% Since the type is non-empty, give a witness
#	mouse : rat
#	%+RESUME
#	%+END rat
#
# Processed in block mode gives us the contents of the BEGIN/END block
# named rat in the file rat.tex. This file looks like:
#
#	%%%
#	%
#	% rat.tex
#	%
#	% Generated from t.pvs
#	%
#	%%%
#	\begin{PVS}
#	rat : TYPE+
#	\end{PVS}
#	Since the type is non-empty, give a witness
#	\begin{PVS}
#	mouse : rat
#	\end{PVS}
#
# Note that the three-percent comments are deleted, and the one-percent
# comments are preserved as normal LaTeX text. The actual PVS code is
# set in the PVS environment.
#
# The same PVS file processed in file mode yields the three-percent
# comment text and ignores the contents of the IGNORE/RESUME block:
#
#	\typeout{t.pvs}%
#	This is a long and tedious PVS file.
#	A type of rodent.
#	\begin{PVS}
#	rat : TYPE+
#	\end{PVS}
#
# It's a good idea to nest BEGIN/END and IGNORE/RESUME properly, but not
# required by the current implementation. You can not nest BEGIN/ENDs,
# though, so you must do something like
#	%+BEGIN outer
#	...
#	%+END outer
#	%+BEGIN inner
#	...
#	%+END inner
#	%+BEGIN outer
#	%%%%%% This appends to the earlier text from the previous outer
#	...
#	%+END outer
#
###

###
#
# Index mode
#
# Index mode works in file mode by compiling a list of all the LEMMAs
# (theorems, etc.) in the PVS file and outputting a list of them at the
# end of the processed file. This can be considered an index to the
# theory. Every lemma can have a (short) description associated with
# it that appears in the index and nowhere else. In addition, individual
# lines of the theory can be marked with the comment %+INDEX, which means
# that they should be added to the index as well. Descriptions can be
# applied to these indexed lines as well. It is normal to INDEX only
# the declaration of a function. 
#
# %+INDEX
#   Add the line this is on to the index. %+INDEX is the only comment
#   that is recognized elsewhere than the leftmost column.
# %+DESC
#   The remainder of the comment line is a textual description of the
#   next LEMMA or INDEX item that occurs. Multiple %+DESC comments with no
#   intervening LEMMA or INDEX are concatenated.
#
# Typical use would be:
#	%+DESC Transitivity of divides-by, poorly defined.
#	LEMMA t6p : d(p,q) AND d(q,r) => d(p,r)
#	%+DESC States that a witch must be made of
#	%+DESC wood, which is lighter than a duck, and therefore
#	%+DESC witches float and must be burnt.
#	which(w:Witch)(d:Duck) : bool = %+INDEX
#		weight(w) > mass(d) ;
#
#
###

###
#
# Usage
#
# This LPP (delit.pl) understands the following flags:
# -d	Debugging mode. May be repeated to increase debug output.
# -t	Preserve leading tab. Otherwise, leading tabs in PVS output are
#	stripped in order to line up the indentation properly (probably
#	only applicable to my own style of PVS indenting)
# -i	Also output index information.
# -I	Output the index and nothing else.
# -f	Set file mode.
# -b	Set block mode.
# -x	Set strip mode (strip all comments and output plain PVS)
# -p d	Set directory to d for output.
#
# One of -f, -b or -x _must_ be set. -i and -I only mean something
# in file mode.
#
#
# After the option flags (which must occur in the order given here, 
# if at all), give delit.pl the name(s) of the PVS file(s) to  process.
#
###

###
#
# beginning of program code
#
$debug=0;
$LOG_ENTER=2;
$TEX_VERBOSITY=1;
$mode=0;
$preservetab=0;
$indexprop=0;
$desc="";
$prefix="";
@props=();

sub usage
{
	print <<EOF;
Usage: delit.pl [-d]... [-t][-iI] [-p dir] <-b|-f|-x> file...

-d	Increases debugging level; may be repeated
-t	Preserve leading tab
-i      Write out index of lemmas and INDEX. This is only relevant in -f mode.
-I      Write out index of lemmas and INDEX _ONLY_. No other output.
-p dir  Set directory, prepended to block names to produce filenames.
-b	Write out %+BEGIN %+END blocks
-f	Write out entire file
-x	Write out collapsed PVS file

	One of -b, -x or -f MUST be given.
EOF
	die "Missing a required parameter.\n";
}

usage unless (@ARGV);

while ($ARGV[0] eq "-d") 
{
	$debug++;
	shift @ARGV;
}

if ($ARGV[0] eq "-t")
{
	$preservetab=1;
	shift @ARGV;
}
if ($ARGV[0] eq "-i")
{
	$indexprop=1;
	shift @ARGV;
}
if ($ARGV[0] eq "-I")
{
	$indexprop=2;
	shift @ARGV;
}
if ($ARGV[0] eq "-p")
{
	$prefix="$ARGV[1]/";
	shift @ARGV;
	shift @ARGV;
}


$mode=1 if ($ARGV[0] eq "-b");
$mode=2 if ($ARGV[0] eq "-f");
$mode=3 if ($ARGV[0] eq "-x");

usage unless ($mode);

shift @ARGV;

#
# These are constant strings that probably don't need to be changed.
#
$endpvsblock="\\end{PVS}\n";
$beginpvsblock="\\begin{PVS}\n";

%extantfiles=();

sub addProp($$)
{
my ($line,$prop);
$line=$_[0];
$prop=$_[1];

if ($prop)
{
	$line =~ s/=.*//;
}
else
{
	$line =~ s/:.*//;
}

if ($desc)
{
	print "Adding $desc to $line\n" if ($debug);
	$line .= "\\emph{$desc}";
	$desc="";
}
push @props,$line;
}

sub pvsfile($)
{
my ($filename,$inblock,$inpvs) ;
my $inspace;
$filename=shift;
#
# Inblock tells us we're within a %+BEGIN %+END section;
# Inpvs switches between straight text output (for comment lines)
# and special PVS output.
#
$inpvs=0;
$inspace=0;


$basedir=$filename;
$basedir =~ s/[^\/]+$//;
$basename=$filename;
$basename =~ s/$basedir// if $basedir;
$basedir=$prefix if $prefix;

if ($mode==2)
{
	$inblock=1;
	$outfile="${basedir}pvs-$basename";
	$outfile =~ s/.pvs$/.tex/;
}
else
{
	$inblock=0;
	$outfile="-";
}
open INFILE,$filename or die "Can't open $filename\n";
open OUTFILE,">$outfile" or die "Can't get $outfile\n";

print OUTFILE "\\typeout{$filename}%\n";

$line=0;
while (<INFILE>)
{
	$line++;
	# Get rid of leading and trailing whitespace.
	chomp;
	$org=$_;
	s/^\s+//;
	chomp;
	next if (/^$/ && $inspace);
	$inspace= /^$/;

	# Always record DESC entries
	if (/^%\+DESC/)
	{
		$desc=$_;
		$desc =~ s/^%.DESC\s*//;
		printf "Set DESC=$desc\n" if ($debug);
		next;
	}
	#
	# Skip section headers
	#
	if ($mode==2)
	{
		$inblock=0 if /^%\+I/;	# IGNORE
		$inblock=1 if /^%\+R/;	# RESUME
		next if (/^%\+/);
		next if (/^%\s+/);
		s/^%%//;
		# next if (/^%\s/);
	}
	next if (/^%%%/);

	# Index even in IGNORE blocks
	if ($indexprop)
	{
		for $i ("LEMMA","THEOREM","AXIOM")
		{
			if (/$i/) 
			{
				addProp($_,0)
			}
		}
		addProp($_,1),s/%+INDEX// if (/%+INDEX/) ;
	}

	# Only process the rest if we're not in an ignore block
	next unless $inblock or $mode != 2;

	#
	# Handle mismatched BEGINS or ENDS depending on
	# where we are (in or out of a block) and set inblock
	# accordingly.
	#
	if (/^%\+[BE]/ && $mode!=2)
	{
		@files=split /\s+/;
		if (@files > 1)
		{
			$newout="$basedir$files[1]";
			chomp $newout;
			$newout.= ".tex";
		}
		else
		{
			$newout="-";
		}

		if ($inblock)
		{
			print STDERR "In block $_\n" if ($debug > $LOG_ENTER);
			die "Unmatched %+BEGIN in $filename (line $line)\n" 
				if (/^%\+B/);
			$inblock=0 if (/^%\+E/);
			print OUTFILE $endpvsblock if ($inpvs) ;
			$inpvs=0;
			die "Filename $newout doesn't match begin $outfile (line $line)\n"
				unless ($newout eq $outfile) ;
		}
		else
		{
			print STDERR "Out of block $_\n" if ($debug > $LOG_ENTER);
			$inblock=1 if (/^%\+B/) ;
			die "Unmatched %+END in $filename (line $line)\n" 
				if (/^%\+E/);
			print STDERR "File=$newout\n";
			print OUTFILE "\\typeout{$filename, line $.}%\n" if ($debug > $TEX_VERBOSITY); 
			if (exists $extantfiles{$newout})
			{
				open OUTFILE,">>$newout" or 
					die "Can't re-open $newout\n";
			}
			else
			{
				open OUTFILE,">$newout" or 
					die "Can't open $newout\n";
				$extantfiles{$newout}=$filename;
				print OUTFILE<<EOF;
%%%
%
% $newout
%
% Generated from $filename
%
%%%
EOF
			}
			$outfile=$newout;
		}
	}
	# print "H: $_\n";

	next if (/^%\+/);
	next unless ($inblock);

	#
	# We now have a real line to process (since we're in
	# a block) This if handles changes from PVS to non-PVS
	# blocks gracefully.
	#

	if ($inpvs)
	{
		if (/^%/)
		{
			print OUTFILE $endpvsblock unless ($indexprop==2);
			$inpvs=0;
		}
	}
	else
	{
		chomp;
		next if (/^$/);
		unless (/^%/)
		{
			print OUTFILE $beginpvsblock unless ($indexprop==2);
			$inpvs=1;
		}
	}
	# Get rid of spaces, one leading tab, then
	# replace tabs with some standard indentation
	$_=$org;
	s/\t// unless $preservetab;
	s/\t/    /g;
	s/^%+\s*//;
	print OUTFILE "$_\n" unless ($indexprop == 2);

}

if ($inpvs)
{
	print OUTFILE $endpvsblock unless ($indexprop==2);
	$inpvs=0;
}

close INFILE;

if ($indexprop && ($mode==2) && (@props))
{
print OUTFILE "\\PVSIndex\n\\begin{itemize}\n";
for (@props)
{
	print OUTFILE "\\item[DEFN]" if (/:/) ;
	print OUTFILE "\\item[LEMMA]" unless (/:/) ;
	print OUTFILE "$_\n";
}
print OUTFILE "\\end{itemize}\n";
}

}

sub texfile($)
{
my $filename=shift;
open INFILE,$filename or die "Can't open $filename\n";

while(<INFILE>)
{
	print;
}

close INFILE;
}

sub pvsXfile($)
{
	my $filename=shift;
	my $outfile=$filename;
	my $blank;

	$outfile =~ s/.pvs$/.tex/;
	open INFILE,$filename or die "Can't open $filename\n";
	open OUTFILE,">pvs-x$outfile" or die "Can't write $outfile\n";

	
	print OUTFILE $beginpvsblock;
	$blank=1;
	while(<INFILE>)
	{
		next if /^%/;
		chomp;
		if (/^$/)
		{
			print OUTFILE "\n" unless $blank;
			$blank=1;
		}
		else
		{
			print OUTFILE "$_\n";
			$blank=0;
		}
	}
	print OUTFILE $endpvsblock;

	close INFILE;
	close OUTFILE;
}


for (@ARGV)
{
	$.=0;
	if (/\.pvs$/)
	{
		pvsfile ($_) unless ($mode==3);
		pvsXfile ($_) if ($mode==3);
	}
	else
	{
		texfile ($_) ;
	}
}
