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

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import us.freeandfair.corla.model.ContestToAudit.AuditType;
import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * The Department of State dashboard.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
// this is an unusual entity, in that it is a singleton; it thus has only one
// possible id (0).
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "dos_dashboard")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class DoSDashboard implements PersistentEntity, Serializable {  
  /**
   * The DoS dashboard ID (it is a singleton).
   */
  public static final Long ID = Long.valueOf(0);
  
  /**
   * The minimum number of random seed characters.
   */
  public static final int MIN_SEED_LENGTH = 20;
  
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 
  
  /**
   * The ID. This is always 0, because this object is a singleton.
   */
  @Id
  private Long my_id = ID;
  
  /**
   * The version (for optimistic locking).
   */
  @Version
  @SuppressWarnings("PMD.UnusedPrivateField")
  private Long my_version;
  
  /**
   * The contests to be audited and the reasons for auditing.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "my_dashboard", 
             fetch = FetchType.LAZY, orphanRemoval = true)
  @Column(name = "contest_to_audit")
  private Set<ContestToAudit> my_contests_to_audit = new HashSet<>();

  /**
   * The audit stage of the election.
   */
  @Enumerated(EnumType.STRING)
  private AuditStage my_audit_stage = AuditStage.PRE_AUDIT;
  
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
  public DoSDashboard() {
    super();
  }
  
  /**
   * @return the database ID for this dashboard, which is the same as
   * its county ID.
   */
  @Override
  public Long id() {
    return my_id;
  }
  
  /**
   * Sets the database ID for this dashboard.
   * 
   * @param the_id The ID, effectively ignored; the database ID for a DoS 
   * dashboard is always 0.
   * @exception IllegalArgumentException if the ID is not 0.
   */
  @Override
  public final void setID(final Long the_id) {
    if (!ID.equals(the_id)) {
      throw new IllegalArgumentException("the only valid ID for a DoSDashboard is 0");
    }
    my_id = ID;
  }

  /**
   * @return the version for this dashboard.
   */
  @Override
  public Long version() {
    return my_version;
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
  public void setRiskLimitForComparisonAudits(final BigDecimal the_risk_limit) {
    my_risk_limit_for_comparison_audits = the_risk_limit;
  }
  
  /**
   * Update the audit status of a contest. 
   * 
   * @param the_contest_to_audit The new status of the contest to audit.
   */
  //@ requires the_contest_to_audit != null;
  public void updateContestToAudit(final ContestToAudit the_contest_to_audit) {
    // check to see if the contest is in our set
    final Set<ContestToAudit> contests_to_remove = new HashSet<>();
    for (final ContestToAudit c : my_contests_to_audit) {
      if (c.contest().equals(the_contest_to_audit.contest())) {
        // flag the entry for removal
        contests_to_remove.add(c);
        c.setDashboard(null);
      }
    }
    my_contests_to_audit.removeAll(contests_to_remove);
    if (the_contest_to_audit.audit() != AuditType.NONE) {
      the_contest_to_audit.setDashboard(this);
      my_contests_to_audit.add(the_contest_to_audit);
    }
  }
  
  /**
   * @return the current set of contests to audit. This is an unmodifiable
   * set; to update, use updateContestToAudit().
   */
  public Set<ContestToAudit> contestsToAudit() {
    return Collections.unmodifiableSet(my_contests_to_audit);
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
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "DoSDashboard [county=" + id() + "]";
  }

  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = true;
    if (the_other instanceof DoSDashboard) {
      final DoSDashboard other_ddb = (DoSDashboard) the_other;
      // there can only be one DoS dashboard in the system for each
      // ID, so we check their equivalence by ID
      result &= nullableEquals(other_ddb.id(), id());
    } else {
      result = false;
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
