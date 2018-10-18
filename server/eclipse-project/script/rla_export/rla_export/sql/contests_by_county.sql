-- Contests IDs by county ID
-- FIXME: Needs canonical contest names, which may differ from names assigned in a county

SELECT 
   contest.county_id,
   county.name AS county_name,
   contest.name AS contest_name,
   contest.id AS contest_id
FROM
   contest
LEFT JOIN
   county ON contest.county_id = county.id
ORDER BY county_id, contest_id
;
