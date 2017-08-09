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

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import us.freeandfair.corla.hibernate.AbstractEntity;

/**
 * An administrator in the system. 
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
@Entity
@Table(name = "administrator")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class Administrator extends AbstractEntity {
  /**
   * The username.
   */
  @Column(nullable = false, updatable = false)
  private String my_username;
  
  /**
   * The user's full (display) name.
   */
  @Column(nullable = false, updatable = false)
  private String my_full_name;
  
  /**
   * The two-factor authentication information.
   */
  // TODO this is a placeholder, the final format of this is not yet known
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
}
