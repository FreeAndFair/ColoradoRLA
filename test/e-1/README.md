In arapahoe-missing-two.csv two lines have been removed. One line has been removed that matches the first
random selection. The other, which happens to be right before it, matches a
selection from the next round.
This is if you run the smoke test like this:

    ./main.py -f ../e-1/arapahoe-missing-two.csv -F ../e-1/arapahoe-manifest.csv
    
The missing lines have(would have had) imprinted ids: 10-2-20, 10-2-21.

The results look like this:

    County 3 audit complete, ended after 48 ballots (of 163 exported) and 3 rounds, -1 to go

