/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Aug 11, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Basic behaviors that span all endpoints. In particular, standard exceptional
 * behavior with regards to cross-cutting concerns like authentication or
 * erroneous or bogus requests from clients.
 * 
 * @trace endpoint
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public abstract class AbstractEndpoint implements Endpoint {
  /**
   * The endpoint result for the ongoing transaction.
   */
  protected String my_endpoint_result;

  /**
   * Indicate that an internal server error has taken place.
   */
  public void serverError(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
    Main.LOGGER.error("server error 500: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that some data was not found.
   */
  public void dataNotFound(final Response the_response, 
                           final String the_log_message) {
    the_response.status(HttpStatus.NOT_FOUND_404);
    Main.LOGGER.error("server error 404: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that data that the client provided is ill-formed.
   */
  public void badDataContents(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.UNPROCESSABLE_ENTITY_422);
    Main.LOGGER.error("bad data from client 422: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that the type/shape of data the client provided is ill-formed.
   */
  public void badDataType(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415);
    Main.LOGGER.error("bad data type from client 415: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that the client is not authorized to perform the requested action.
   */
  public void unauthorized(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.UNAUTHORIZED_401);
    Main.LOGGER.error("unauthorized access 401: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate the client has violated an invariant or precondition relating data
   * to the endpoint in question. E.g., a digest is incorrect with regards to
   * the file that it summarizes.
   */
  public void invariantViolation(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.BAD_REQUEST_400);
    Main.LOGGER.error("invariant violation 400: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate the client cannot perform the requested action because it violates
   * the server's state machine.
   */
  public void illegalTransition(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.FORBIDDEN_403);
    Main.LOGGER.error("illegal transition attempt 403: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that the server is temporarily unavailable. This is typically due
   * to a long-lived transaction running or server maintenance.
   */
  public void serverUnavailable(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.SERVICE_UNAVAILABLE_503);
    Main.LOGGER.error("server temporarily unavailable 503: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that the operation completed successfully.
   */
  public void ok(final Response the_response) {
    the_response.status(HttpStatus.OK_200);    
  }
  
  /**
   * Indicate and log that the operation completed successfully.
   */
  public void ok(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.OK_200);
    Main.LOGGER.error("successful operation 200: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * This before-filter is evaluated before each request, and can read the
   * request and read/modify the response. Our before-filter performs
   * authentication checking.
   */
  @SuppressWarnings("PMD.ConfusingTernary")
  @Override
  public void before(final Request the_request, final Response the_response) {
    // Presume everything goes ok.
    ok(the_response);

    // Check to see if the server is operating properly.
    if (!Persistence.hasDB()) {
      serverError(the_response, "no database");
    } else if (Persistence.isTransactionRunning()) {
      serverUnavailable(the_response, "a long transaction is running");
    }
    // Check to see if the requested endpoint is permitted from the current
    // state of the server.
    // else if (false) {
    //   illegalTransition(the_response, "endpoint not permitted by ASM");
    //   return;
    // }

    // This access is well-formed and permitted by the state machine, so log the
    // use of the endpoint.
    Main.LOGGER.info("checking authentication on " + the_request);

    // If the client is unauthorized, then indicate such and redirect.

    // If there is no authentication cookie in the request at all, then simply
    // redirect to the top-level authentication endpoint.
    // @todo kiniry Add once we have cookies turned on.

    // If we have an authentication cookie, then check if the user is
    // authenticated.
    if (!Authentication.authenticate(the_request)) {
      unauthorized(the_response, "client not authenticated to perform this action");
    }
    
    // Validate the parameters of the request.
    if (!validateParameters(the_request)) {
      dataNotFound(the_response, "parameter validation failed");
    }
  }
  
  /**
   * Validates the parameters of a request. The default behavior is to
   * return 'true'.
   * 
   * @param the_request The request.
   * @return true if the parameters are valid, false otherwise.
   */
  protected boolean validateParameters(final Request the_request) {
    return true;
  }
}
