# Test data directory

Contents:

* dominion-2017-CVR_Export_20170310104116.csv: Dominion CVR_Export in csv format
 of Arapahoe County data, sample of 165 ballots. From CDOS

* e-1: directory with one county, 2 or 3 contests

    * Regent contest has a very wide margin of 123-14 out of 165 ballots = 66.1%
 and appears on all CVRs

  	`Sample size =  8 for margin 66.1%, risk 10%, gamma 1.03905, o1 0, o2 0, u1 0, u2 0`
	`Sample size = 10 for margin 66.1%, risk 10%, gamma 1.03905, o1 1, o2 0, u1 0, u2 0`
	`Sample size = 18 for margin 66.1%, risk 10%, gamma 1.03905, o1 0, o2 1, u1 0, u2 0`
	`Sample size =  7 for margin 66.1%, risk 10%, gamma 1.03905, o1 0, o2 0, u1 1, u2 0`
	`Sample size =  6 for margin 66.1%, risk 10%, gamma 1.03905, o1 0, o2 0, u1 0, u2 1`

    * COUNTY COMMISSIONER DISTRICT 3 has a wide margin of (27-12)/46 = 32.6% 
    and does not appear on all the CVRs.
    Without independent evidence of which ballots it is on or how many there
    are, however, we base the diluted margin on all the ballot cards and sample
    uniformly from them all.  That gives a margin of (27-12)/165 = 9.0909% and a sample size of 152:

	`Sample size = 152 for margin 9.09091%, risk 10%, gamma 1.1, or1 0.01, or2 0.01, ur1 0.01, ur2 0.01, roundUp1 1, roundUp2 1`

    * Amendment 107 has a tiny margin of 1 out of 95 = 1.052631%, and does not appear on all the CVRs

        `Sample size = 482 for margin 1.05263%, risk 10%, gamma 1.1, o1 0, o2 0, u1 0, u2 0`

* e-1-12: directory with one county, 12 contests in all, two multi-winner contests, clean cvrs

* e-1-20170310104116: directory with test data for CDOS CVR example, plus matching manifest file

* zoo: directory with a variety of files that should parse cleanly
