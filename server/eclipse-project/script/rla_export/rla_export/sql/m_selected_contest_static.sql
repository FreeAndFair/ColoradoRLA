-- List of contests selected by the Secretary of State for audit, with information about the contest that doesn't change during the audit, namely the reason for the audit, the number of winners allowed in the contest, the tabulated winners of the contest, the numbers of ballots cards recorded as cast in the county (total number as well as the number containing the given contest) and the value of the error inflation factor (gamma).

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
   ccca.gamma
FROM 
   driving_contest AS dc
LEFT JOIN
   county_contest_comparison_audit AS ccca
   ON ccca.contest_id = dc.contest_id
LEFT JOIN
   contest_to_audit AS cta
   ON dc.contest_id = cta.contest_id
LEFT JOIN
   county AS cty ON cty.id = dc.dashboard_id
LEFT JOIN 
   contest AS cn ON cn.id = dc.contest_id
LEFT JOIN
   county_contest_result AS ccr
   ON cn.id = ccr.contest_id

ORDER BY county_name, contest_name
;
