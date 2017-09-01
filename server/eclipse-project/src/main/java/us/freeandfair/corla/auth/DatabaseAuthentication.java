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
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Administrator;
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
  public boolean traditionalAuthenticate(final Request the_request,
                                         final Response the_response,
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
}
