-- List of contests with current status.  Which contests has the  Secretary selected for audit? Which contests (if any) has the  Secretary selected for hand count? How many discrepancies of each type have been found so far?


SELECT 
   cr.contest_name AS contest_name,
   LOWER(ca.audit_reason) as audit_reason,
   --LOWER(cta.audit) AS current_audit_type,
   LOWER(ca.audit_status) as random_audit_status,
   -- cn.votes_allowed,
   -- cn.winners_allowed,
   cr.ballot_count AS ballot_card_count,
   -- cr.contest_ballot_count AS contest_ballot_card_count,   -- need to SUM from county_contest_results
   SUBSTRING(cr.winners, 2, LENGTH(cr.winners) - 2) AS winners,
   cr.min_margin,
   ca.risk_limit,
   ca.audited_sample_count,
   ca.two_vote_over_count,
   ca.one_vote_over_count,
   ca.one_vote_under_count,
   ca.two_vote_under_count,
   ca.disagreement_count,
   ca.other_count,
   ca.gamma,
   ca.overstatements,
   ca.optimistic_samples_to_audit,
   ca.estimated_samples_to_audit
FROM 
   comparison_audit AS ca
--LEFT JOIN
--   contest_to_audit AS cta
--   on ca.contest_id = cta.contest_id    -- now pick up via contests_to_contest_results
-- LEFT JOIN 
--   contest AS cn ON cn.id = ca.contest_id -- now pick up via contests_to_contest_results
LEFT JOIN
   contest_result AS cr
   ON ca.contest_result_id = cr.id
ORDER BY contest_name
;
