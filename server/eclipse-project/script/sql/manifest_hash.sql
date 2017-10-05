-- For each county, ballot manifest hash and county name

SELECT co.name as county_name, uf.hash AS ballot_manifest_hash
FROM county AS co
LEFT JOIN uploaded_file AS uf 
ON co.id = uf.county_id
WHERE (hash_status = 'VERIFIED' AND status = 'IMPORTED_AS_BALLOT_MANIFEST') OR uf.county_id is NULL 
ORDER BY county_name
;
