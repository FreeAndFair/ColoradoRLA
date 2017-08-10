/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * The Department of State dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
// TODO this is an unusual entity in that it should really be a singleton; 
// there may be a better way to handle this than what is being done now.
@Entity
@Table(name = "dos_dashboard")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class DepartmentOfStateDashboard extends AbstractEntity implements Serializable {  
  /**
   * The minimum number of random seed characters.
   */
  public static final int MIN_SEED_LENGTH = 20;
  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 
  
  /**
   * The contests to be audited and the reasons for auditing.
   */
  @ElementCollection
  @CollectionTable(name = "dos_dashboard_contest_audit_reason",
                   joinColumns = @JoinColumn(name = "dos_dashboard_id", 
                                             referencedColumnName = "my_id"))
  @MapKeyJoinColumn(name = "contest_id")
  @Column(name = "audit_reason")
  private Map<Contest, AuditReason> my_audit_reasons = 
      new HashMap<Contest, AuditReason>();

  /**
   * The audit stage of the election.
   */
  @Enumerated(EnumType.STRING)
  private AuditStage my_audit_stage = AuditStage.INITIAL;
  
  /**
   * The set of contests to be hand counted.
   */
  @ElementCollection
  @CollectionTable(name = "dos_dashboard_hand_count_contest",
                   joinColumns = @JoinColumn(name = "dos_dashboard_id", 
                                             referencedColumnName = "my_id"))
  @Column(name = "contest_id")
  private Set<Contest> my_hand_count_contests = 
      new HashSet<Contest>();
  
  /**
   * The risk limit for comparison audits.
   */
  private BigDecimal my_risk_limit_for_comparison_audits;
  
  /**
   * The random seed.
   */
  private String my_random_seed;
  
  /**
   * Constructs a new Department of State dashboard with default values.
   */
  // if we delete this constructor, we get warned that each class should
  // define at least one constructor; we can't win in this situation.
  @SuppressWarnings("PMD.UnnecessaryConstructor")
  public DepartmentOfStateDashboard() {
    super();
  }
  
  /**
   * Checks the validity of a random seed. To be valid, a random seed must
   * have at least MIN_SEED_CHARACTERS characters, and all characters must
   * be digits.
   * 
   * @param the_seed The seed.
   * @return true if the seed meets the validity requirements, false otherwise.
   */
  public static boolean isValidSeed(final String the_seed) {
    boolean result = true;
    
    if (the_seed != null && the_seed.length() >= MIN_SEED_LENGTH) {
      for (final char c : the_seed.toCharArray()) {
        if (!Character.isDigit(c)) {
          result = false;
          break;
        }
      }
    } else {
      result = false;
    }
    
    return result;
  }
  
  /**
   * @return the current stage of the audit.
   */
  public AuditStage auditStage() {
    return my_audit_stage;
  }
  
  /**
   * Sets the audit stage.
   * 
   * @param the_audit_stage The new audit stage.
   */ 
  public void setAuditStage(final AuditStage the_audit_stage) {
    my_audit_stage = the_audit_stage;
  }
  
  /**
   * @return the risk limit for comparison audits, or null if none has been set.
   */
  public BigDecimal getRiskLimitForComparisonAudits() {
    return my_risk_limit_for_comparison_audits;
  }
  
  /**
   * Sets the risk limit for comparison audits.
   * 
   * @param the_risk_limit The risk limit.
   */
  //@ requires auditStage() == AuditStage.AUTHENTICATED;
  //@ ensures auditStage() == AuditStage.RISK_LIMITS_SET;
  public void setRiskLimitForComparisonAudits(final BigDecimal the_risk_limit) {
    my_risk_limit_for_comparison_audits = the_risk_limit;
  }
  
  /**
   * Select the contests to audit.
   * 
   * @param the_contests A map from the contests to audit to reasons for 
   * auditing them.
   */
  public void selectContestsToAudit(final Map<Contest, AuditReason> the_contests) {
    my_audit_reasons.putAll(the_contests);
  }
  
  /**
   * Select a contest for full hand count.
   * 
   * @param the_contest The contest.
   */
  public void selectContestForHandCount(final Contest the_contest) {
    my_hand_count_contests.add(the_contest);
  }
  
  /**
   * Sets the random seed.
   * 
   * @param the_seed The random seed.
   */
  public void setRandomSeed(final String the_random_seed) {
    my_random_seed = the_random_seed;
  }
  
  /**
   * @return the random seed.
   */
  public String randomSeed() {
    return my_random_seed;
  }
}
