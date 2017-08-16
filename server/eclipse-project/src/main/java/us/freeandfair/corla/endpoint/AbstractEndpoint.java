/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 11, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.eclipse.jetty.http.HttpStatus;

import spark.Request;
import spark.Response;
import spark.Spark;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.json.Result;
import us.freeandfair.corla.model.Administrator.AdministratorType;
import us.freeandfair.corla.model.LogEntry;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

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
@SuppressFBWarnings({"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", 
// Justification: False positive because we are weaving in behavior
// in before() to initialize my_persistent_asm_state.
    "SF_SWITCH_NO_DEFAULT"})
// Justification: False positive; there is a default case.
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.TooManyMethods"})
public abstract class AbstractEndpoint implements Endpoint {
  /**
   * A flag that disables ASM checks, when true.
   */
  public static final boolean DISABLE_ASM = false;
  
  /**
   * The ASM for this endpoint.
   */
  protected AbstractStateMachine my_asm;
  
  /**
   * The endpoint result for the ongoing transaction.
   */
  protected String my_endpoint_result;
  
  /**
   * The log entries to be logged by this endpoint after execution.
   */
  protected List<LogEntry> my_log_entries = new ArrayList<>();
  
  /**
   * Halts the endpoint execution by ending the request and returning the
   * most recently set response code and endpoint result.
   */
  protected final void halt(final Response the_response) {
    Spark.halt(the_response.status(), my_endpoint_result);
  }

  /**
   * @return the abstract state machine class for this endpoint. By default,
   * the endpoint does not have an abstract state machine.
   */
  // this method is not empty!
  @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
  protected Class<? extends AbstractStateMachine> asmClass() {
    return null;
  }
  
  /**
   * Gets the identity of the ASM used by this endpoint for this
   * request (as necessary). By default, the endpoint does not have
   * an abstract state machine.
   * 
   * @param the_request The request.
   * @return The identity of the ASM used by this endpoint for this
   * request. 
   */
  // this method is not empty!
  @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
  protected String asmIdentity(final Request the_request) {
    return null;
  }
 
  /**
   * Which event does this endpoint wish to take? By default, it
   * does not execute an event.
   * 
   * @return the event.
   */
  // this method is not empty!
  @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
  protected ASMEvent endpointEvent() {
    return null;
  }
  
  /**
   * Load the appropriate ASM from the database and make sure that
   * the transition we wish to take is legal.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @SuppressWarnings("unused")
  protected void loadAndCheckASM(final Request the_request,
                                 final Response the_response) {
    // get the state of the ASM
    if (DISABLE_ASM || asmClass() == null || endpointEvent() == null) {
      // there is no ASM event for this endpoint
      return;
    }
    my_asm = ASMUtilities.asmFor(asmClass(), asmIdentity(the_request));
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
   * 
   * @param the_response The response.
   * @return true if the ASM transitioned successfully
   */
  @SuppressWarnings("unused")
  protected boolean transitionAndSaveASM(final Response the_response)  {
    if (DISABLE_ASM || my_asm == null || endpointEvent() == null) {
      // there is no ASM event for this endpoint
      return true;
    }
    try {
      my_asm.stepEvent(endpointEvent());
    } catch (final IllegalStateException e) {
      illegalTransition(the_response, e.getMessage());
      return false;
    }
    return ASMUtilities.save(my_asm);
  }
  
  /**
   * Indicate that the operation completed successfully.
   * @param the_response the HTTP response.
   */
  public void ok(final Response the_response) {
    the_response.status(HttpStatus.OK_200);   
    my_endpoint_result = "";
  }
  
  /**
   * Indicate and log that the operation completed successfully.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void ok(final Response the_response, 
                 final String the_body) {
    okJSON(the_response, Main.GSON.toJson(new Result(the_body)));
  }

  /**
   * Indicate and log that the operation completed succesfully, and
   * send the specified JSON-formatted string.
   * 
   * @param the_response The HTTP response.
   * @param the_json The JSON string to send as the body of the response.
   */
  public void okJSON(final Response the_response, final String the_json) {
    the_response.status(HttpStatus.OK_200);
    the_response.body(the_json);
    Main.LOGGER.info("successful operation 200 on endpoint " + endpointName());
    my_endpoint_result = the_json;
  }
  /**
   * Indicate the client has violated an invariant or precondition relating data
   * to the endpoint in question. E.g., a digest is incorrect with regards to
   * the file that it summarizes.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void invariantViolation(final Response the_response, 
                                 final String the_body) {
    the_response.status(HttpStatus.BAD_REQUEST_400);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("invariant violation 400 on endpoint " + endpointName());
  }
  
  /**
   * Indicate that the client is not authorized to perform the requested action.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void unauthorized(final Response the_response, 
                           final String the_body) {
    the_response.status(HttpStatus.UNAUTHORIZED_401);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("unauthorized access 401 on endpoint " + endpointName());
    // TODO: should we halt() the endpoint execution here and simply send 
    // the response
  }

  /**
   * Indicate the client cannot perform the requested action because it violates
   * the server's state machine.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void illegalTransition(final Response the_response, 
                                final String the_body) {
    the_response.status(HttpStatus.FORBIDDEN_403);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("illegal transition attempt 403 on endpoint " + 
        endpointName());
  }

  /**
   * Indicate that some data was not found.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void dataNotFound(final Response the_response, 
                           final String the_body) {
    the_response.status(HttpStatus.NOT_FOUND_404);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("server error 404 on endpoint " + endpointName());
  }

  /**
   * Indicate that the type/shape of data the client provided is ill-formed.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void badDataType(final Response the_response, 
                          final String the_body) {
    the_response.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("bad data type from client 415 on endpoint " + 
        endpointName());
  }

  /**
   * Indicate that data that the client provided is ill-formed.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void badDataContents(final Response the_response, 
                              final String the_body) {
    the_response.status(HttpStatus.UNPROCESSABLE_ENTITY_422);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("bad data from client 422 on endpoint " + endpointName());
  }

  /**
   * Indicate that an internal server error has taken place.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void serverError(final Response the_response, 
                          final String the_body) {
    the_response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("server error 500 on endpoint " + endpointName());
  }

  /**
   * Indicate that the server is temporarily unavailable. This is typically due
   * to a long-lived transaction running or server maintenance.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void serverUnavailable(final Response the_response, 
                                final String the_body) {
    the_response.status(HttpStatus.SERVICE_UNAVAILABLE_503);
    my_endpoint_result = Main.GSON.toJson(new Result(the_body));
    the_response.body(my_endpoint_result);
    Main.LOGGER.error("server temporarily unavailable 503 on endpoint " + 
        endpointName());
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

    // If this is the root endpoint, just return.
    if ("/".equals(endpointName())) {
      return;
    }

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
    
    // Load and check the ASM
    loadAndCheckASM(the_request, the_response);
  } 
  
  /**
   * The after filter for this endpoint. By default, it attempts to commit 
   * any open transaction (this makes writing endpoint code more straightforward,
   * as the vast majority of endpoints will never have to deal with transactions
   * themselves).
   */
  public void after(final Request the_request, final Response the_response) {
    // try to take the transition for this endpoint in the ASM and save it to the DB
    // note that we do not try to commit when we have an error code in the response
    if (the_response.status() == HttpStatus.OK_200 && 
        transitionAndSaveASM(the_response) && 
        Persistence.isTransactionActive()) {
      try {
        // since the transition finished, let's commit
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
    } else {
      if (Persistence.canTransactionRollback()) {
        try {
          Persistence.rollbackTransaction();
        } catch (final PersistenceException ex) {
          Main.LOGGER.error("could not roll back transaction for error response: " +
                            ex.getMessage());
        }
      } else {
        Main.LOGGER.error("could not roll back transaction for error response");
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
          
      case NONE:

      default:
    }
    
    return result;
  }
}
