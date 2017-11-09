-- Show status of uploaded files

SELECT cty.name, filename, hash_status, approximate_record_count AS approx_count, size, status, timestamp
FROM uploaded_file AS uf
LEFT JOIN
  county as cty ON cty.id = uf.county_id
ORDER BY status, hash_status, timestamp
;
