-- List of contests with current status.  Which contests has the  Secretary selected for audit? Which contests (if any) has the  Secretary selected for hand count? How many discrepancies of each type have been found so far?


SELECT 
   ccr.contest_name AS contest_name,
   LOWER(ccca.audit_reason) as audit_reason,
   --LOWER(cta.audit) AS current_audit_type,
   LOWER(ccca.audit_status) as random_audit_status,
   -- cn.votes_allowed,
   -- cn.winners_allowed,
   ccr.ballots_count AS ballot_card_count,
   -- ccr.contest_ballot_count AS contest_ballot_card_count,
   SUBSTRING(ccr.winners, 2, LENGTH(ccr.winners) - 2) AS winners,
   ccr.min_margin,
   ccca.risk_limit,
   ccca.two_vote_over_count,
   ccca.one_vote_over_count,
   ccca.one_vote_under_count,
   ccca.two_vote_under_count,
   ccca.gamma
FROM 
   comparison_audit AS ccca
--LEFT JOIN
--   contest_to_audit AS cta
--   on ccca.contest_id = cta.contest_id    now use contests_to_contest_results?
-- LEFT JOIN 
--   contest AS cn ON cn.id = ccca.contest_id
LEFT JOIN
   contest_result AS ccr  -- was county_contest_result
   ON ccca.contest_result_id = ccr.id
ORDER BY contest_name
;
