-- For each county and each batch, compare batch counts reflected in manifest vs CVR file.

SELECT
   co.name AS county_name,
   bmi.scanner_id,
   bmi.batch_id,
   bmi.batch_size as count_per_manifest,
   count(cvr.id) as count_per_cvr_file,
   (bmi.batch_size -count(cvr.id)) as difference

FROM ballot_manifest_info AS bmi
 LEFT JOIN cast_vote_record AS cvr
        ON bmi.county_id = cvr.county_id 
            AND bmi.batch_id = cvr.batch_id
            AND bmi.scanner_id = cvr.scanner_id
 LEFT JOIN county as co ON bmi.county_id = co.id
WHERE cvr.record_type='UPLOADED' 
GROUP BY county_name, bmi.scanner_id, bmi.batch_id, bmi.batch_size
ORDER BY county_name, bmi.scanner_id, bmi.batch_id, bmi.batch_size
;
