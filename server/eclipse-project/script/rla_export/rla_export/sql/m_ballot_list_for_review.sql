-- List of ballot cards to audit per county, in the order in which the Audit Board reviews them, namely, by round and, within each round, by tabulator, batch and position within batch.


SELECT DISTINCT
cty.name AS county_name,
MIN(rnd.number) AS round,	
cvr_s.scanner_id,
cvr_s.batch_id,
cvr_s.record_id,
cvr_s.imprinted_id,
cvr_s.ballot_type
 FROM round AS rnd
  INNER JOIN 
(cvr_audit_info AS cai
 LEFT JOIN cast_vote_record AS cvr_s
   ON cai.cvr_id = cvr_s.id
 LEFT JOIN county AS cty
   ON cai.dashboard_id = cty.id)
 ON rnd.start_audit_prefix_length <= cai.index 
 AND cai.index < rnd.expected_audited_prefix_length
  GROUP BY county_name, scanner_id, batch_id, record_id, imprinted_id, ballot_type
  ORDER BY county_name, round, scanner_id, batch_id, record_id
;
