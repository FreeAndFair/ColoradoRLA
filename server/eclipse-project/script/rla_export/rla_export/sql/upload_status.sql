-- Show status of uploaded files

SELECT county_id, filename, hash_status, approximate_record_count AS approx_count, size, status, timestamp
FROM uploaded_file
ORDER BY status, hash_status, timestamp
;
