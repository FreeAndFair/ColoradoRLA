#!/usr/bin/env python
"""Bootstrap a Dominion CVR file
Adapted from 
 Bootstrap a very large data file | Tomochika Fujisawa's site
 https://tmfujis.wordpress.com/2016/06/24/bootstrap-a-very-large-data-file/
"""

import sys
import numpy

for line in open(sys.argv[1], "r"):
        cnt = numpy.random.poisson(lam=1)

        if cnt == 0:
                continue
        else:
                for i in range(cnt):
                        sys.stdout.write(line)
