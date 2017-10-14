-- Ballot cards and selections to audit per county

SELECT
 co.name as county_name,
 COUNT ( cai.cvr_id ) as selections_to_audit,
 COUNT ( DISTINCT cai.cvr_id) as unique_ballot_cards_to_audit
FROM county AS co
LEFT JOIN cvr_audit_info AS cai
ON co.id = cai.dashboard_id
GROUP BY co.name ORDER BY co.name
;
