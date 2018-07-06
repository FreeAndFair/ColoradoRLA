/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @model_review Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * The Department of State dashboard.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// this is an unusual entity, in that it is a singleton; it thus has only one
// possible id (0).
@Entity
@Cacheable(true)
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
  private Long my_version;
  
  /**
   * The contests to be audited and the reasons for auditing.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "contest_to_audit",
                   joinColumns = @JoinColumn(name = "dashboard_id", 
                                             referencedColumnName = "my_id"))
  private Set<ContestToAudit> my_contests_to_audit = new HashSet<>();
  
  /**
   * The election info.
   */
  @Embedded
  private AuditInfo my_audit_info = new AuditInfo();
  
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
   * @return the audit info.
   */
  public AuditInfo auditInfo() {
    return my_audit_info;
  }
  
  /**
   * Updates the audit info, using the non-null fields of the specified 
   * AuditInfo. This method does not do any sanity checks on the fields;
   * it is assumed that they are checked by the caller.
   * 
   * @param the_new_info The new info.
   */
  public void updateAuditInfo(final AuditInfo the_new_info) {
    my_audit_info.updateFrom(the_new_info);
  }
  
  /**
   * Removes all contests to audit for the specified county. This is 
   * typically done if the county re-uploads their CVRs (generating new
   * contest information).
   * 
   * @param the_county The county.
   * @return true if any contests to audit were removed, false otherwise.
   */
  public boolean removeContestsToAuditForCounty(final County the_county) {
    boolean result = false;
    
    final Set<ContestToAudit> contests_to_remove = new HashSet<>();
    for (final ContestToAudit c : my_contests_to_audit) {
      if (c.contest().county().equals(the_county)) {
        contests_to_remove.add(c);
        result = true;
      }
    }
    my_contests_to_audit.removeAll(contests_to_remove);
    
    return result;
  }

  /**
   * Remove all ContestsToAudit from this dashboard
   **/
  public void removeContestsToAudit(){
    my_contests_to_audit.clear();
  }

  /**
   * Update the audit status of a contest. 
   * 
   * @param the_contest_to_audit The new status of the contest to audit.
   * @return true if the contest was already being audited or hand counted, 
   * false otherwise.
   */
  //@ requires the_contest_to_audit != null;
  public boolean updateContestToAudit(final ContestToAudit the_contest_to_audit) {
    boolean auditable = true;
    
    // check to see if the contest is in our set
    ContestToAudit contest_to_remove = null;
    for (final ContestToAudit c : my_contests_to_audit) {
      if (c.contest().equals(the_contest_to_audit.contest())) {
        // check if the entry is auditable; if so, it will be removed later
        auditable = !c.audit().equals(AuditType.NOT_AUDITABLE);
        contest_to_remove = c;
        break;
      }
    }
    
    if (auditable) {
      my_contests_to_audit.remove(contest_to_remove);
      if (the_contest_to_audit.audit() != AuditType.NONE) {
        my_contests_to_audit.add(the_contest_to_audit);
      }
    }
    
    return auditable;
  }
  
  /**
   * @return the current set of contests to audit. This is an unmodifiable
   * set; to update, use updateContestToAudit().
   */
  public Set<ContestToAudit> contestsToAudit() {
    return Collections.unmodifiableSet(my_contests_to_audit);
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
      result &= nullableEquals(other_ddb.contestsToAudit(), contestsToAudit());
      result &= nullableEquals(other_ddb.auditInfo(), auditInfo());
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
    return nullableHashCode(auditInfo());
  }
}
