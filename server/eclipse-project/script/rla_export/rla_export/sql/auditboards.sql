-- Audit Board membership by county
-- Within each county, audit boards are listed by descending order of sign-in time.

SELECT co.name AS county_name, ab.members AS member, ab.sign_in_time, ab.sign_out_time
FROM county AS co
LEFT JOIN audit_board as ab
ON co.id = ab.dashboard_id
ORDER BY county_name, sign_in_time DESC
;
