-- Retrieve counts of audited ballot cards and each type of discrepancy by contest
-- along with contest ballot counts, outcomes and margins for checking calculations.

SELECT
  contest.name,
  contest.id,
  contest.winners_allowed,
  county_contest_comparison_audit.one_vote_over_count,
  county_contest_comparison_audit.one_vote_under_count,
  county_contest_comparison_audit.two_vote_over_count,
  county_contest_comparison_audit.two_vote_under_count,
  county_contest_comparison_audit.id,
  county_contest_comparison_audit.audit_reason,
  county_contest_comparison_audit.audit_status,
  county_contest_comparison_audit.audited_sample_count,
  county_contest_comparison_audit.disagreement_count,
  county_contest_comparison_audit.estimated_samples_to_audit,
  county_contest_comparison_audit.estimated_recalculate_needed,
  county_contest_comparison_audit.gamma,
  county_contest_comparison_audit.optimistic_recalculate_needed,
  county_contest_comparison_audit.optimistic_samples_to_audit,
  county_contest_comparison_audit.risk_limit,
  contest.county_id,
  county_contest_result.min_margin,
  county_contest_result.winners,
  county_contest_result.losers,
  county_contest_result.county_ballot_count,
  county_contest_result.contest_ballot_count
FROM
  county_contest_comparison_audit,
  contest,
  county_contest_result
WHERE
  county_contest_comparison_audit.contest_id = contest.id AND
  county_contest_comparison_audit.contest_result_id = county_contest_result.id
ORDER BY contest.county_id
;
