-- List, by county name, of prefix length (longest initial subsequence, without breaks, of random sequence for which each card in the subsequence has already been audited).

SELECT co.name as county_name, cd.audited_prefix_length 
FROM county AS co
LEFT JOIN county_dashboard AS cd
ON co.id = cd.county_id
ORDER BY co.name
;
