-- For each contest, and for each cast vote record in the random sequence
-- (with duplicates) for which the Audit Board has submitted information, and which 
-- contain the contest in question,
-- original cvr info, audit board interp info.
-- note that the random sequence index (includes dupes) is contest_audit_info.index
-- cvr_contest_info.index is the index of the *contest* on the ballot
-- Note that in case of an overvote, `cci_a.choices` shows all the choices the Audit Board thought the voter intended, while cci.choices will *not* show all those choices. 


SELECT 
   cty.name AS county_name, 
   cn.name AS contest_name, 
   cai.index + 1 AS random_sequence_index,
   cvr_s.imprinted_id,
   cvr_s.ballot_type, 
   cci.choices AS choice_per_voting_computer, 
   cci_a.choices AS choice_per_audit_board,
   cci_a.consensus AS did_audit_board_agree,
   cci_a.comment AS audit_board_comment,
   cvr_a.timestamp

FROM 
   cvr_audit_info AS cai
 LEFT JOIN
   cvr_contest_info AS cci
   ON cci.cvr_id = cai.cvr_id 
 LEFT JOIN cast_vote_record AS cvr_s
   ON cai.cvr_id = cvr_s.id
 LEFT JOIN cvr_contest_info AS cci_a
   ON cai.acvr_id = cci_a.cvr_id
     AND cci_a.contest_id = cci.contest_id
 LEFT JOIN 
   cast_vote_record AS cvr_a
   ON cai.acvr_id = cvr_a.id
 LEFT JOIN
   contest AS cn
   ON cci.contest_id = cn.id
 LEFT JOIN county AS cty
   ON cai.dashboard_id = cty.id

WHERE cai.counted = 't' 

ORDER BY county_name, contest_name, random_sequence_index
;
