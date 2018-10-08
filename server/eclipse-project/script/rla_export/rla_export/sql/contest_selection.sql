-- List of selections from the random sample for the given contest

-- Listed in random selection order in the contest_cvr_ids field, for verification
-- of the random selection procedure.  The values there can also be
-- correlated with cvr_id in contest_comparison export, and with audited_cvr_count.

SELECT
   min_margin,
   contest_name,
   contest_cvr_ids
FROM
   contest_result
;
