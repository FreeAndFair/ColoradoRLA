-- Ballot_sequence and audit_subsequence by county and round

SELECT
   co.name AS county_name,
   r.number AS round_number,
   r.ballot_sequence,
   r.audit_subsequence
FROM
   round AS r
LEFT JOIN county as co ON r.dashboard_id = co.id
ORDER BY county_name, round_number
;
