-- List of all contests, with information about the contest that doesn't change
-- during the audit, namely the reason for the audit, the number of winners
-- allowed in the contest, the tabulated winners of the contest, the numbers
-- of ballots cards recorded as cast in the county (total number as well as
-- the number containing the given contest) and the values of the
-- risk limit and error inflation factor (gamma).

SELECT 
   cty.name AS county_name, 
   cn.name AS contest_name,
   ccca.audit_reason,
   cn.votes_allowed,
   cn.winners_allowed,
   ccr.winners,
   ccr.min_margin,
   ccr.county_ballot_count AS county_ballot_card_count,
   ccr.contest_ballot_count AS contest_ballot_card_count,
   ccca.risk_limit,
   ccca.gamma
FROM 
   county_contest_comparison_audit AS ccca
LEFT JOIN
   contest_to_audit AS cta
   ON ccca.contest_id = cta.contest_id
LEFT JOIN
   county AS cty ON cty.id = ccca.dashboard_id
LEFT JOIN 
   contest AS cn ON cn.id = ccca.contest_id
LEFT JOIN
   county_contest_result AS ccr
   ON cn.id = ccr.contest_id

ORDER BY county_name, contest_name
;
