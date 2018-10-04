-- Election Outcomes, Counts and Margins

SELECT
  -- county.name AS county_name,
  contest_result.contest_name AS contest_name,
  contest_vote_total.choice AS choice,
  contest_vote_total.vote_total AS votes
FROM
  contest_vote_total,
  contest_result
WHERE
  contest_vote_total.result_id = contest_result.id
ORDER BY
  contest_result.contest_name ASC,
  contest_vote_total.vote_total DESC;
