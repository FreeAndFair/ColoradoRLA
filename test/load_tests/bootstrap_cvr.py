#!/usr/bin/env python
"""Bootstrap a Dominion CVR file
Takes lines from an existing CVR file and bootstraps them to the desired length.

Bootstrapping is designed to retain the original flavor of CVRs, by picking
each line a random number of times in order to closely approximate the
desired length.

The initial columns are modified, to follow the standard numbering scheme.
But every CVR is labeled as TabulatorNum 1, BatchId 1.
The CvrNumber and RecordId fields both count from 1 to the overall target number.
An ImprintedId to match is also generated.

Usage:

 python bootstrap_cvr.py 100

generates a 100-CVR file based on the hardcoded D0FILE constant.

Note: if you don't get the desired length, you'll get a message saying to
try running it again.

Note: this is a rough approximation. The last few lines may be omitted or be
underrepresented in the sample.

Adapted from 
 Bootstrap a very large data file | Tomochika Fujisawa's site
 https://tmfujis.wordpress.com/2016/06/24/bootstrap-a-very-large-data-file/
"""

import sys
import numpy

D0FILE="../dominion-2017-CVR_Export_20170310104116.csv"
D0COUNT=165

def find_nth(s, x, n=0, overlap=False):
    """Find the nth occurrence of the string x in s
    From @Mark Byers and @Stefan: https://stackoverflow.com/a/23479065/507544
    """
    l = 1 if overlap else len(x)
    i = -l
    for c in xrange(n + 1):
        i = s.find(x, i + l)
        if i < 0:
            break
    return i

target = int(sys.argv[1])

inHeader = True
cvrNumber = 1

for line in open(D0FILE, "r"):
        if inHeader:
                sys.stdout.write(line)
                if line.startswith('"CvrNumber"'):
                        inHeader = False
                continue

        cnt = numpy.random.poisson(lam=1.0 * target/(D0COUNT-1)) #FIXME: hard-code one less than len of assumed input file

        if cnt == 0:
                continue
        else:
                for i in range(cnt):
                        cvrline = '"%d","1","1","%d","1-1-%d"%s' % \
                                  (cvrNumber, cvrNumber, cvrNumber, line[find_nth(line, ',', 4):])
                        sys.stdout.write(cvrline)
                        cvrNumber += 1
                        if cvrNumber == target + 1:
                                sys.exit(0)


if cvrNumber < target:
        sys.stderr.write("Only got %d CVRs. Re-run the command\n" % cvrNumber)
