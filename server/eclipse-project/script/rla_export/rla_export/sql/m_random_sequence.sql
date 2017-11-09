-- Random sequence of ballot cards in each county



SELECT
cty.name AS county_name,
cai.index + 1 AS random_sequence_index,	
cvr_s.scanner_id,
cvr_s.batch_id,
cvr_s.record_id,
cvr_s.imprinted_id,
cvr_s.ballot_type
 FROM cvr_audit_info AS cai
 LEFT JOIN cast_vote_record AS cvr_s
   ON cai.cvr_id = cvr_s.id
 LEFT JOIN county AS cty
   ON cai.dashboard_id = cty.id
 ORDER BY county_name, random_sequence_index
;
