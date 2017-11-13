-- Ballot_sequence and audit_subsequence by county and round

SELECT
   dashboard_id AS county_id,
   r.number AS round_number,
   r.ballot_sequence,
   r.audit_subsequence
FROM
   round AS r
ORDER BY dashboard_id, round_number
;
