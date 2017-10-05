-- Retrieve counts of audited ballot cards and each type of discrepancy by contest

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
  county_contest_comparison_audit.dashboard_id
FROM 
  public.county_contest_comparison_audit, 
  public.contest
WHERE 
  county_contest_comparison_audit.contest_id = contest.id
;
