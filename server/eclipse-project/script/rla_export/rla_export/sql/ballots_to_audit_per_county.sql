-- Numbers of ballot cards and selections to audit per county

SELECT
 co.name as county_name,
 COUNT ( cai.cvr_id ) as selections_to_audit,
 COUNT ( DISTINCT cai.cvr_id) as unique_ballot_cards_to_audit
FROM  cvr_audit_info AS cai
LEFT JOIN county AS co
ON co.id = cai.dashboard_id
ORDER BY county_name
;
