-- Election Outcomes, Counts and Margins

SELECT 
  county.name AS county_name,
  contest.name AS contest_Name, 
  county_contest_vote_total.choice AS choice, 
  county_contest_vote_total.vote_total AS votes,
  contest.votes_allowed,
  county_contest_result.winners,
  county_contest_result.min_margin,
  county_contest_result.county_ballot_count,
  county_contest_result.contest_ballot_count
FROM 
  public.county_contest_vote_total, 
  public.county_contest_result, 
  public.contest, 
  public.county
WHERE 
  county_contest_vote_total.result_id = county_contest_result.id AND
  county_contest_result.contest_id = contest.id AND
  county_contest_result.county_id = county.id
ORDER BY
  contest.county_id ASC, 
  contest.id ASC, 
  county_contest_vote_total.vote_total DESC;
