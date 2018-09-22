-- List of contests with current status.  Which contests has the  Secretary selected for audit? Which contests (if any) has the  Secretary selected for hand count? How many discrepancies of each type have been found so far?


SELECT 
   cty.name AS county_name, 
   cn.name AS contest_name,
   LOWER(ccca.audit_reason) as audit_reason,
   LOWER(cta.audit) AS current_audit_type,
   LOWER(ccca.audit_status) as random_audit_status,
   cn.votes_allowed,
   cn.winners_allowed,
   ccr.county_ballot_count AS county_ballot_card_count,
   ccr.contest_ballot_count AS contest_ballot_card_count,
   SUBSTRING(ccr.winners, 2, LENGTH(ccr.winners) - 2) AS winners,
   ccr.min_margin,
   ccca.risk_limit,
   ccca.two_vote_over_count,
   ccca.one_vote_over_count,
   ccca.one_vote_under_count,
   ccca.two_vote_under_count,
   ccca.gamma
FROM 
   county_contest_comparison_audit AS ccca
LEFT JOIN
   contest_to_audit AS cta
   on ccca.contest_id = cta.contest_id
LEFT JOIN
   county AS cty ON cty.id = ccca.dashboard_id
LEFT JOIN 
   contest AS cn ON cn.id = ccca.contest_id
LEFT JOIN
   county_contest_result AS ccr
   ON cn.id = ccr.contest_id
ORDER BY county_name, contest_name
;
