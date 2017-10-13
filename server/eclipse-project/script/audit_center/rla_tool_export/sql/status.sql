-- Status by contest
-- Status: NOT_STARTED, IN_PROGRESS, RISK_LIMIT_ACHIEVED, ENDED.
--  ENDED means "ended without achieving the risk limit"
--  (either because it was aborted and sent to hand count, or aborted for some
--  other reason - currently not possible - or because it was an opportunistic
--  contest and the county's audit ended).

SELECT cta.contest_id, cta.audit, cta.reason, ccca.audit_reason
FROM contest_to_audit AS cta
LEFT JOIN county_contest_comparison_audit AS ccca
ON cta.contest_id = ccca.id
;
