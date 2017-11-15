-- Contests IDs by county ID

SELECT 
   contest.county_id,
   contest.id AS contest_id
FROM
   contest
ORDER BY county_id, contest_id
;
