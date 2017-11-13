-- Contests IDs by county ID

SELECT 
   county_id,
   id AS contest_id
FROM
   contest
ORDER BY county_id, contest_id
;
