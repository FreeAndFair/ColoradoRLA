-- List of contests selected by Secretary of State for audit, with current status.  Which contests has the  Secretary selected for audit? Which contests (if any) has the  Secretary selected for hand count? How many discrepancies of each type have been found so far?


SELECT 
   cty.name AS county_name, 
   cn.name AS contest_name,
   cta.audit AS current_audit_type,
   ccca.audit_status as random_audit_status,
   ccca.two_vote_over_count,
   ccca.one_vote_over_count,
   ccca.one_vote_under_count,
   ccca.two_vote_under_count
FROM 
   driving_contest AS dc
LEFT JOIN
   county_contest_comparison_audit AS ccca
   ON ccca.contest_id = dc.contest_id
LEFT JOIN
   contest_to_audit AS cta
   on dc.contest_id = cta.contest_id
LEFT JOIN
   county AS cty ON cty.id = dc.dashboard_id
LEFT JOIN 
   contest AS cn ON cn.id = dc.contest_id
ORDER BY county_name, contest_name
;
