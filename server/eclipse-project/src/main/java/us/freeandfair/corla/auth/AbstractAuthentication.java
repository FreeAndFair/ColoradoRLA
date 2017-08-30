/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 29, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.auth;

import javax.persistence.PersistenceException;

import spark.Request;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.AdministratorQueries;

/**
 * An abstract base class that enforces the two-stage state machine for two-factor
 * authentication.
 * 
 * @author Joseph R. Kiniry
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.StdCyclomaticComplexity"})
public abstract class AbstractAuthentication implements AuthenticationInterface {  
  /**
   * Authenticate the administrator `the_username` with credentials
   * `the_password` (for traditional authentication) or `the_second_factor`
   * (for two-factor authentication).  This method should be called twice in
   * succession, first for traditional authentication and second for two-factor
   * authentication.
   * 
   * @trace authentication.authenticate_county_administrator
   * @trace authentication.authenticate_state_administrator
   * @return true iff authentication succeeds.
   * @param the_request The request.
   * @param the_admin_type the type of administrator to attempt to authenticate.
   * @param the_username the username of the person to attempt to authenticate.
   * @param the_password the password for `username`.
   * @param the_second_factor the second factor for `username`.
   */
  @Override
  public boolean authenticateAdministrator(final Request the_request,
                                           final AdministratorType the_admin_type,
                                           final String the_username, 
                                           final String the_password,
                                           final String the_second_factor) {
    boolean result = true;
    String auth_stage = 
        the_request.session().attribute(AuthenticationInterface.AUTH_STAGE);
    if (auth_stage == null) {
      auth_stage = AuthenticationInterface.PASSWORD;
    }
    try {
      // If we didn't get a well-formed request in the first place, fail.
      if (the_username == null || the_username.isEmpty()) {
        result = false;
      } else {
        // Check to see if we have not started the authentication process at all.
        if (auth_stage.equals(AuthenticationInterface.PASSWORD) || 
            // If we are already authenticated in the session and the client is asking
            // to authenticate again, then wipe the old session and try to authenticate.
            auth_stage.equals(AuthenticationInterface.ADMIN)) {
          if (traditionalAuthenticate(the_request, the_username, the_password)) {
            // We have traditionally authenticated.
            final Administrator admin = 
                AdministratorQueries.byUsername(the_username);
            admin.updateLastLoginTime();
            Persistence.saveOrUpdate(admin);
            the_request.session().attribute(AuthenticationInterface.AUTH_STAGE,
                                            AuthenticationInterface.SECOND_FACTOR);
            Main.LOGGER.info("Traditional authentication succeeded for administrator " + 
                the_username);
          } else {
            Main.LOGGER.info("Traditional authentication failed for administrator " + 
                the_username);
            result = false;
          }
        } else if (auth_stage.equals(AuthenticationInterface.SECOND_FACTOR)) {
          // Or are we half-way through it?
          if (secondFactorAuthenticate(the_request,
                                       the_username,
                                       the_second_factor)) {
            // We have both traditionally and second-factor authenticated.
            final Administrator admin = 
                AdministratorQueries.byUsername(the_username);
            admin.updateLastLoginTime();
            Persistence.saveOrUpdate(admin);
            the_request.session().attribute(AuthenticationInterface.AUTH_STAGE, 
                                            AuthenticationInterface.ADMIN);
            the_request.session().attribute(AuthenticationInterface.ADMIN, 
                                            admin);
            Main.LOGGER.info("Second factor authentication succeeded for administrator " + 
                the_username);
          } else {
            // Send the authentication state machine back to its initial state.
            the_request.session().attribute(AuthenticationInterface.AUTH_STAGE, 
                                            AuthenticationInterface.PASSWORD);
            Main.LOGGER.info("Second factor authentication failed for administrator" + 
                the_username);
            result = false;
          }
        }
      }
    } catch (final PersistenceException e) {
      // there's nothing we can really do here other than saying that the
      // authentication failed; it's also possible we failed to update the last
      // login time, but that's not critical
      deauthenticate(the_request, the_username);
    }

    if (!result) {
      // a failed authentication attempt removes any existing session authentication 
      deauthenticate(the_request, the_username);
      Main.LOGGER.info("Authentication failed for user " + the_username);
    }

    return result;
  }
}
