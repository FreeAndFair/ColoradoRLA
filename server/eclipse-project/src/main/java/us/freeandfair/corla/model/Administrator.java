/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

import us.freeandfair.corla.persistence.AbstractEntity;

/**
 * An administrator in the system. 
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "administrator",
       indexes = { @Index(name = "idx_admin_username", 
                          columnList = "username", unique = true) })
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Administrator extends AbstractEntity implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1; 
  
  /**
   * The username.
   */
  @Column(name = "username", unique = true, nullable = false, updatable = false)
  private String my_username;
  
  /**
   * The administrator type.
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private AdministratorType my_type;
  
  /**
   * The user's full (display) name.
   */
  @Column(nullable = false, updatable = false)
  private String my_full_name;
  
  /**
   * The two-factor authentication information.
   */
  // TODO this is a placeholder, the final format of this is not yet known
  @Column(nullable = false, updatable = false)
  private String my_two_factor_auth_info;
  
  /**
   * The last login time.
   */
  private Instant my_last_login_time;
  
  /**
   * Constructs a new Administrator with default values.
   */
  public Administrator() {
    super();
  }
  
  /**
   * Constructs a new Administrator with the specified values.
   * 
   * @param the_username The username.
   * @param the_full_name The full name.
   * @param the_two_factor_auth_info The two-factor authentication information.
   * @param the_last_login_time The last login time.
   */
  public Administrator(final String the_username, 
                       final String the_full_name, 
                       final String the_two_factor_auth_info, 
                       final Instant the_last_login_time) {
    super();
    my_username = the_username;
    my_full_name = the_full_name;
    my_two_factor_auth_info = the_two_factor_auth_info;
    my_last_login_time = the_last_login_time;
  }
  
  /**
   * @return the username.
   */
  public String username() {
    return my_username;
  }
  
  /**
   * @return the type.
   */
  public AdministratorType type() {
    return my_type;
  }
  
  /**
   * @return the full name.
   */
  public String fullName() {
    return my_full_name;
  }
  
  /**
   * @return the two factor authentication information.
   */
  public String twoFactorAuthInfo() {
    return my_two_factor_auth_info;
  }
  
  /**
   * @return the last login time.
   */
  public Instant lastLoginTime() {
    return my_last_login_time;
  }
  
  /**
   * Updates the last login time to the current time.
   */
  public void updateLastLoginTime() {
    my_last_login_time = Instant.now();
  }
  
  /**
   * @return a String representation of this contest.
   */
  @Override
  public String toString() {
    return "Administrator [username=" + my_username + ", type=" + 
           my_type + ", full_name=" + my_full_name + ", last_login_time = " + 
           my_last_login_time + "]";
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
    if (the_other instanceof Administrator) {
      final Administrator other_admin = (Administrator) the_other;
      result &= nullableEquals(other_admin.id(), id());
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
    return id().hashCode();
  }

  /**
   * Types of administrator.
   */
  public enum AdministratorType {
    COUNTY, STATE;
  }
}
