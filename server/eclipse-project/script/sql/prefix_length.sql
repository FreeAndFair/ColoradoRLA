-- List, by county name, of prefix length (to end of current round)

SELECT co.name as county_name, cd.audited_prefix_length 
FROM county AS co
LEFT JOIN county_dashboard AS cd
ON co.id = cd.county_id
ORDER BY co.name
;
