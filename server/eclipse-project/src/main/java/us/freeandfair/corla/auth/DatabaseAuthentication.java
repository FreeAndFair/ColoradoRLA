/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.auth;

import spark.Request;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.query.AdministratorQueries;

/**
 * A demonstration implementation of AuthenticationInterface used during
 * development to mock an actual back-end authentication system.
 * @trace authentication
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class DatabaseAuthentication extends AbstractAuthentication
    implements AuthenticationInterface {
  /**
   * {@inheritDoc}
   */
  @Override
  public void logoutAdministrator(final Request the_request,
                                  final String the_username) {
    Main.LOGGER.info("Logging out administrator `" + the_username + "'");
    deauthenticate(the_request, the_username);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean secondFactorAuthenticate(final Request the_request,
                                          final String the_username, 
                                          final String the_second_factor) {
    // skip, as we do not implement a second factor in test mode
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean secondFactorAuthenticated(final Request the_request,
                                           final String the_username) {
    final String auth_stage = 
        the_request.session().attribute(AuthenticationInterface.AUTH_STAGE);
    return auth_stage != null && 
        auth_stage.equals(AuthenticationInterface.ADMIN);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean traditionalAuthenticate(final Request the_request,
                                         final String the_username, 
                                         final String the_password) {
    final Administrator admin = 
        AdministratorQueries.byUsername(the_username);
    return admin != null; 
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean traditionalAuthenticated(final Request the_request,
                                          final String the_username) {
    final String auth_stage = 
        the_request.session().attribute(AuthenticationInterface.AUTH_STAGE);
    return auth_stage != null && 
        (auth_stage.equals(AuthenticationInterface.SECOND_FACTOR) ||
            auth_stage.equals(AuthenticationInterface.ADMIN));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAuthenticatedAs(final Request the_request,
                                   final AdministratorType the_type,
                                   final String the_username) {
    boolean result = false;
    final Object admin_attribute = the_request.session().attribute(ADMIN);
    if (admin_attribute instanceof Administrator) {
      final Administrator admin = (Administrator) admin_attribute;
      result = admin.type() == the_type;
      the_request.session().attribute(ADMIN, admin);
    } else if (admin_attribute != null) {
      // this should never happen since we control what's in the session object,
      // but if it does, we'll clear out that attribute and thereby force another
      // authentication
      Main.LOGGER.error("Invalid admin type detected in session.");
      deauthenticate(the_request, the_username);
    }
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void deauthenticate(final Request the_request,
                                final String the_username) {
    traditionalDeauthenticate(the_request, the_username);
    twoFactorDeauthenticate(the_request, the_username);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void traditionalDeauthenticate(final Request the_request,
                                           final String the_username) {
    the_request.session().removeAttribute(ADMIN);
    Main.LOGGER.info("session is now traditionally deauthenticated");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void twoFactorDeauthenticate(final Request the_request,
                                         final String the_username) {
    the_request.session().removeAttribute(ADMIN);
    Main.LOGGER.info("session is now second factor deauthenticated");
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public County authenticatedCounty(final Request the_request) {
    County result = null;
    if (isAuthenticatedAs(the_request, AdministratorType.COUNTY,
                    the_request.queryParams(AuthenticationInterface.USERNAME))) {
      final Administrator admin = 
          (Administrator) the_request.session().attribute(ADMIN);
      if (admin != null) {
        result = admin.county();
      }
    }
    return result;
  }
}
