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

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;
import spark.Spark;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.PersistentASMStateQueries;

/**
 * Basic behaviors that span all endpoints. In particular, standard exceptional
 * behavior with regards to cross-cutting concerns like authentication or
 * erroneous or bogus requests from clients.
 * 
 * @trace endpoint
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public abstract class AbstractEndpoint implements Endpoint {
  /**
   * The endpoint result for the ongoing transaction.
   */
  protected String my_endpoint_result;
  
  /**
   * The ASM for this endpoint, load in the `before` of the endpoint
   * and saved in the `after`.
   */
  protected AbstractStateMachine my_asm;
  
  /**
   * Halts the endpoint execution by ending the request and returning the
   * most recently set response code and endpoint result.
   */
  protected final void halt(final Response the_response) {
    Spark.halt(the_response.status(), my_endpoint_result);
  }

  /**
   * @return The classname of the ASM used by this endpoint.
   */
  protected abstract Class<? extends AbstractStateMachine> asmClass(); 
  
  /**
   * Load the appropriate ASM from the database and make sure that
   * the transition we wish to take is legal.
   */
  protected void loadAndCheckASM(final Response the_response) {
    // get the state of the DoS dashboard
    try {
      my_asm = (AbstractStateMachine) (asmClass().getConstructor().newInstance());
    } catch (final Exception e) {
      // something went horribly wrong
    }
    final PersistentASMState state = 
        PersistentASMStateQueries.get(asmClass(), null);
    state.applyTo(my_asm);
    // check that we are in the right ASM state
    if (!my_asm.enabledASMEvents().contains(endpointEvent())) {
      illegalTransition(the_response,
                        endpointName() + 
                        "attempted to apply illegal event " + endpointEvent() + 
                        " from state " + my_asm.currentState());
    }
  }
  
  /**
   * Save the ASM back to the database.
   */
  protected void transitionAndSaveASM()  {
    my_asm.stepEvent(endpointEvent());
    final PersistentASMState new_state = PersistentASMState.stateFor(my_asm);
    Persistence.saveOrUpdate(new_state);
  }
  
  /**
   * Which event does this endpoint wish to take?
   * @todo kiniry We may in the end just automatically look this up in
   * the ASMEventTo
   */
  protected abstract ASMEvent endpointEvent();

  /**
   * Indicate that the operation completed successfully.
   */
  public void ok(final Response the_response) {
    the_response.status(HttpStatus.OK_200);    
  }
  
  /**
   * Indicate and log that the operation completed successfully.
   */
<<<<<<< HEAD
  public void unauthorized(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.UNAUTHORIZED_401);
    Main.LOGGER.error("unauthorized access 401: " + the_log_message);
    // TODO: should we halt() the endpoint execution here and simply send the response
=======
  public void ok(final Response the_response, final String the_log_message) {
    the_response.status(HttpStatus.OK_200);
    Main.LOGGER.error("successful operation 200: " + the_log_message);
>>>>>>> Implemented automatic ASM load, transition, and save for endpoints.
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate the client has violated an invariant or precondition relating data
   * to the endpoint in question. E.g., a digest is incorrect with regards to
   * the file that it summarizes.
   */
  public void invariantViolation(final Response the_response, 
                                 final String the_log_message) {
    the_response.status(HttpStatus.BAD_REQUEST_400);
    Main.LOGGER.error("invariant violation 400: " + the_log_message);
    my_endpoint_result = the_log_message;
  }
  
  /**
   * Indicate that the client is not authorized to perform the requested action.
   */
  public void unauthorized(final Response the_response, 
                           final String the_log_message) {
    the_response.status(HttpStatus.UNAUTHORIZED_401);
    Main.LOGGER.error("unauthorized access 401: " + the_log_message);
    // TODO: should we halt() the endpoint execution here and simply send 
    // the response
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate the client cannot perform the requested action because it violates
   * the server's state machine.
   */
  public void illegalTransition(final Response the_response, 
                                final String the_log_message) {
    the_response.status(HttpStatus.FORBIDDEN_403);
    Main.LOGGER.error("illegal transition attempt 403: " + the_log_message);
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
   * Indicate that the type/shape of data the client provided is ill-formed.
   */
  public void badDataType(final Response the_response, 
                          final String the_log_message) {
    the_response.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415);
    Main.LOGGER.error("bad data type from client 415: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that data that the client provided is ill-formed.
   */
  public void badDataContents(final Response the_response, 
                              final String the_log_message) {
    the_response.status(HttpStatus.UNPROCESSABLE_ENTITY_422);
    Main.LOGGER.error("bad data from client 422: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that an internal server error has taken place.
   */
  public void serverError(final Response the_response, 
                          final String the_log_message) {
    the_response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
    Main.LOGGER.error("server error 500: " + the_log_message);
    my_endpoint_result = the_log_message;
  }

  /**
   * Indicate that the server is temporarily unavailable. This is typically due
   * to a long-lived transaction running or server maintenance.
   */
  public void serverUnavailable(final Response the_response, 
                                final String the_log_message) {
    the_response.status(HttpStatus.SERVICE_UNAVAILABLE_503);
    Main.LOGGER.error("server temporarily unavailable 503: " + the_log_message);
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

    // Start a transaction, if the database is functioning; otherwise abort
    if (Persistence.hasDB()) {
      Persistence.beginTransaction(); 
    } else {
      serverError(the_response, "no database");
      halt(the_response);
    } 

    // Check that the user is authorized for this endpoint
    if (!checkAuthorization(the_request, requiredAuthorization())) {
      unauthorized(the_response,
                   "client not authorized to perform this action");
      halt(the_response);
    }
    
    // Validate the parameters of the request.
    if (!validateParameters(the_request)) {
      dataNotFound(the_response, "parameter validation failed");
      halt(the_response);
    }
  } 
  
  /**
   * The after filter for this endpoint. By default, it attempts to commit 
   * any open transaction (this makes writing endpoint code more straightforward,
   * as the vast majority of endpoints will never have to deal with transactions
   * themselves).
   */
  public void after(final Request the_request, final Response the_response) {
<<<<<<< HEAD
=======
    // first, take the transition for this endpoint in the ASM and save it to the DB
    transitionAndSaveASM();
    // then finish the transaction
>>>>>>> Implemented automatic ASM load, transition, and save for endpoints.
    if (Persistence.isTransactionRunning()) {
      try {
        Persistence.commitTransaction();
      } catch (final RollbackException e) {
        // this is an internal server error because we don't know what didn't
        // get committed
        serverError(the_response, 
                    "could not commit changes to persistent storage");
        halt(the_response);
        try {
          Persistence.rollbackTransaction();
        } catch (final PersistenceException ex) {
          Main.LOGGER.error("could not roll back failed transaction: " + ex.getMessage());
        }
      }
    }
  }
  
  /**
   * @return the type of authorization required to use this endpoint.
   * The default is NONE.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.NONE;
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
  
  /**
   * Checks to see that the specified request satisfies the specified
   * authorization type.
   * 
   * @param the_request The request.
   * @param the_type The authorization type.
   * @return true if the request is appropriately authorized, false otherwise.
   */
  //@ require the_session != null
  //@ require the_type != null
  public static boolean checkAuthorization(final Request the_request, 
                                           final AuthorizationType the_type) {
    boolean result = true;
    final boolean state = 
        Authentication.isAuthenticatedAs(the_request, AdministratorType.STATE);
    final boolean county =
        Authentication.isAuthenticatedAs(the_request, AdministratorType.COUNTY);

    switch (the_type) {
      case STATE: 
        result = state;
        break;
      
      case COUNTY:
        result = county;
        break;
          
      case EITHER:
        result = county || state;
        break;
          
<<<<<<< HEAD
=======
      case NONE:
>>>>>>> Implemented automatic ASM load, transition, and save for endpoints.
      default:
    }
    
    return result;
  }
}
