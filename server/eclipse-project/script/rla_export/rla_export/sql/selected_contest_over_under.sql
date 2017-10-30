-- For each contest under audit, number of two-vote overstatements, one-vote overstatements, two-vote understatements and one-vote understatements found so far.


SELECT 
	cty.name AS county_name, 
	cst.name as contest_name, 
	ccca.one_vote_over_count,
	ccca.one_vote_under_count,
	ccca.two_vote_over_count,
	ccca.two_vote_under_count
FROM 
	county_contest_comparison_audit AS ccca
LEFT JOIN 
	contest AS cst ON cst.id = ccca.contest_id
LEFT JOIN
	county AS cty ON cty.id = cst.county_id
ORDER BY county_name, contest_name
;
