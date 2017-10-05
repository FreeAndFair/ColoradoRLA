-- List, by county name, of prefix length (to end of current round)
-- FIXME - is this true?  In a county that doesn't have an audit, ignore the prefix_length of 1.

SELECT co.name as county_name, count(*) as prefix_length 
FROM county AS co
LEFT JOIN cvr_audit_info AS cai
ON co.id = cai.dashboard_id
GROUP BY co.name ORDER BY co.name
;
