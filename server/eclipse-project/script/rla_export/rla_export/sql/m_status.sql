-- Status by contest
-- Status: NOT_STARTED, IN_PROGRESS, RISK_LIMIT_ACHIEVED, ENDED.
--  ENDED means "ended without achieving the risk limit"
--  (either because it was aborted and sent to hand count, or aborted for some
--  other reason - currently not possible - or because it was an opportunistic
--  contest and the county's audit ended). Before a user from the Department of State
--  clicks "Launch Audit" this field is blank.

SELECT cty.name as county_name, ctt.name as contest_name, ccca.audit_status
FROM contest_to_audit AS cta
LEFT JOIN county_contest_comparison_audit AS ccca
ON cta.contest_id = ccca.contest_id
LEFT JOIN contest AS ctt
ON ctt.id = cta.contest_id
LEFT JOIN county as cty
ON ctt.county_id = cty.id
WHERE cta.audit = 'COMPARISON'
ORDER BY county_name, contest_name
;
