-- Show information about all ACVR entries. In particular:
--  For all random selections, compare ACVRs with their CVRs, by contest
-- Note that a "discrepancy" column entry appears if ANY of the contests on the ACVR have a discrepancy
-- Note that some CVRs may be selected multiple times, and each selection shows up here.
--  The RLA algorithm takes matches and discrepancies into account for each selection.

SELECT cai.index as selection, dashboard_id AS county, imprinted_id, record_type, timestamp, counted, disagreement,
   discrepancy, cci_a.comment, cci_a.consensus, cci_s.contest_id, cai.cvr_id, cci_s.choices as machine_choices, acvr_id, cci_a.choices as audit_board_choices
 FROM cvr_audit_info AS cai
 LEFT JOIN cast_vote_record AS cvr
   ON cai.acvr_id = cvr.id
 LEFT JOIN cvr_contest_info AS cci_s
   ON cai.cvr_id = cci_s.cvr_id
 LEFT JOIN cvr_contest_info AS cci_a
   ON cai.acvr_id = cci_a.cvr_id
     AND cci_a.contest_id = cci_s.contest_id
 ORDER BY cai.index, cci_s.contest_id 
;