/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 11, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.model.Administrator.AdministratorType.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.HibernateException;

import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Spark;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.auth.AuthenticationInterface;
import us.freeandfair.corla.json.Result;
import us.freeandfair.corla.model.Administrator;
import us.freeandfair.corla.model.LogEntry;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.LogEntryQueries;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * Basic behaviors that span all endpoints. In particular, standard exceptional
 * behavior with regards to cross-cutting concerns like authentication or
 * erroneous or bogus requests from clients.
 * 
 * @trace endpoint
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressFBWarnings({"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", 
// Justification: False positive because we are weaving in behavior
// in before() to initialize my_persistent_asm_state.
    "SF_SWITCH_NO_DEFAULT"})
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.TooManyMethods",
    "PMD.EmptyMethodInAbstractClassShouldBeAbstract", "PMD.GodClass"})
public abstract class AbstractEndpoint implements Endpoint {
  /**
   * A flag that disables ASM checks, when true.
   */
  public static final boolean DISABLE_ASM = false;
  
  /**
   * The number of times to retry committing logs for failed transactions.
   */
  public static final int LOG_COMMIT_RETRIES = 5;
  
  /**
   * The "Retry-After" value for a transaction failure response, in seconds.
   */
  public static final String RETRY_AFTER_DELAY = "10";

  /**
   * The ASM for this endpoint.
   */
  protected ThreadLocal<AbstractStateMachine> my_asm = 
      new ThreadLocal<AbstractStateMachine>();
  
  /**
   * The endpoint result for the ongoing transaction.
   */
  protected ThreadLocal<String> my_endpoint_result = new ThreadLocal<String>();
  
  /**
   * The HTTP status for the ongoing transaction.
   */
  protected ThreadLocal<Integer> my_status = new ThreadLocal<Integer>();
  
  /**
   * The log entries to be logged by this endpoint after execution.
   */
  protected ThreadLocal<List<LogEntry>> my_log_entries = 
      new ThreadLocal<List<LogEntry>>();
  
  /**
   * Halts the endpoint execution by ending the request and returning the
   * most recently set response code and endpoint result.
   */
  protected final void halt(final Response the_response) {
    Spark.halt(the_response.status(), my_endpoint_result.get());
  }

  /**
   * @return the abstract state machine class for this endpoint. By default,
   * the endpoint does not have an abstract state machine.
   */
  // this method is not empty!
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
  protected ASMEvent endpointEvent() {
    return null;
  }
  
  /**
   * The endpoint method. Delegates immediately to the child class
   */
  /**
   * Load the appropriate ASM from the database and make sure that
   * the transition we wish to take is legal.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  protected void loadAndCheckASM(final Request the_request,
                                 final Response the_response) {
    // get the state of the ASM
    if (DISABLE_ASM || asmClass() == null) {
      // there is no ASM for this endpoint
      my_asm.set(null);
      return;
    }
    my_asm.set(ASMUtilities.asmFor(asmClass(), asmIdentity(the_request)));
    // check that we are in the right ASM state
    if (endpointEvent() != null && 
        !my_asm.get().enabledASMEvents().contains(endpointEvent())) {
      illegalTransition(the_response,
                        endpointName() + 
                        " attempted to apply illegal event " + endpointEvent() + 
                        " from state " + my_asm.get().currentState());
    }
  }

  /**
   * Save the ASM back to the database.
   * 
   * @param the_response The response.
   * @return true if the ASM transitioned successfully
   */
  protected boolean transitionAndSaveASM(final Response the_response)  {
    if (DISABLE_ASM || my_asm.get() == null || endpointEvent() == null) {
      // there is no ASM event for this endpoint
      return true;
    }
    try {
      // this asmFor() will be a no-op in nearly all cases, but in multi-transaction
      // endpoint hits like uploading large CVR imports, it is possible for the
      // state to change out from underneath us
      my_asm.set(ASMUtilities.asmFor(asmClass(), my_asm.get().identity()));
      my_asm.get().stepEvent(endpointEvent());
    } catch (final IllegalStateException e) {
      illegalTransition(the_response, e.getMessage(), false);
      return false;
    }
    return ASMUtilities.save(my_asm.get());
  }
  
  /**
   * Indicate that the operation completed successfully. This method is only
   * to be used when no response should be sent in the body beyond any 
   * streaming that the endpoint has already done; to provide an OK response
   * outside of a streaming context, use ok(Response, String) or 
   * okJSON(Response, String).
   * 
   * @param the_response the HTTP response.
   */
  public void ok(final Response the_response) {
    my_log_entries.get().add(new LogEntry(HttpStatus.OK_200, endpointName(), Instant.now()));
    my_status.set(HttpStatus.OK_200);
    my_endpoint_result.set("");
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
   * Indicate and log that the operation completed successfully, and
   * send the specified JSON-formatted string.
   * 
   * @param the_response The HTTP response.
   * @param the_json The JSON string to send as the body of the response.
   */
  public void okJSON(final Response the_response, final String the_json) {
    my_log_entries.get().add(new LogEntry(HttpStatus.OK_200, endpointName(), Instant.now()));
    my_status.set(HttpStatus.OK_200);
    my_endpoint_result.set(the_json);
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
    my_log_entries.get().add(new LogEntry(HttpStatus.BAD_REQUEST_400,
                                    "invariant violation on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.BAD_REQUEST_400);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    halt(the_response);
  }
  
  /**
   * Indicate that the client is not authorized to perform the requested action.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void unauthorized(final Response the_response, 
                           final String the_body) {
    my_log_entries.get().add(new LogEntry(HttpStatus.UNAUTHORIZED_401,
                                    "unauthorized access on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.UNAUTHORIZED_401);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    halt(the_response);
  }

  /**
   * Indicate the client cannot perform the requested action because it violates
   * the server's state machine.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   * @param the_halt true to halt, false otherwise.
   */
  public void illegalTransition(final Response the_response, 
                                final String the_body,
                                final boolean the_halt) {
    my_log_entries.get().add(new LogEntry(HttpStatus.FORBIDDEN_403, 
                                    "illegal transition attempt on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.FORBIDDEN_403);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    if (the_halt) {
      halt(the_response);
    }
  }

  /**
   * Indicate the client cannot perform the requested action because it violates
   * the server's state machine.
   * 
   * @param the_response The HTTP response.
   * @param the_body The HTTP response body.
   */
  public void illegalTransition(final Response the_response, final String the_body) {
    illegalTransition(the_response, the_body, true);
  }
  
  /**
   * Indicate that some data was not found.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void dataNotFound(final Response the_response, 
                           final String the_body) {
    my_log_entries.get().add(new LogEntry(HttpStatus.NOT_FOUND_404,
                                    "data not found on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.NOT_FOUND_404);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    halt(the_response);
  }

  /**
   * Indicate that the type/shape of data the client provided is ill-formed.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void badDataType(final Response the_response, 
                          final String the_body) {
    my_log_entries.get().add(new LogEntry(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415,
                                    "bad data type from client on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    halt(the_response);
  }

  /**
   * Indicate that data that the client provided is ill-formed.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void badDataContents(final Response the_response, 
                              final String the_body) {
    my_log_entries.get().add(new LogEntry(HttpStatus.UNPROCESSABLE_ENTITY_422,
                                    "bad data from client on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.UNPROCESSABLE_ENTITY_422);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    halt(the_response);
  }

  /**
   * Indicate that an internal server error has taken place.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void serverError(final Response the_response, 
                          final String the_body) {
    my_log_entries.get().add(new LogEntry(HttpStatus.INTERNAL_SERVER_ERROR_500,
                                    "server error on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.INTERNAL_SERVER_ERROR_500);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    halt(the_response);
  }

  /**
   * Indicate that the server is temporarily unavailable. This is typically due
   * to server maintenance.
   * @param the_response the HTTP response.
   * @param the_body the body of the HTTP response.
   */
  public void serverUnavailable(final Response the_response, 
                                final String the_body) {
    my_log_entries.get().add(new LogEntry(HttpStatus.SERVICE_UNAVAILABLE_503,
                                    "service temporarily unavailable on " + 
                                        endpointName() + ": " + the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.SERVICE_UNAVAILABLE_503);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
    halt(the_response);
  }
  
  /**
   * Indicate that the endpoint action failed due to a transaction failure.
   * Unlike other error responses, this one does _not_ halt the connection.
   * 
   * @param the_response The HTTP response.
   * @param the_body The body of the HTTP response.
   */
  public void transactionFailure(final Response the_response, final String the_body) {
    my_log_entries.get().add(new LogEntry(HttpStatus.SERVICE_UNAVAILABLE_503,
                                    "transaction failure on " + endpointName() + ": " +
                                        the_body,
                                    Instant.now()));
    my_status.set(HttpStatus.SERVICE_UNAVAILABLE_503);
    the_response.header("Retry-After", RETRY_AFTER_DELAY);
    my_endpoint_result.set(Main.GSON.toJson(new Result(the_body)));
  }

  /**
   * This before-filter is evaluated before each request, and can read the
   * request and read/modify the response. Our before-filter performs
   * authentication checking.
   */
  @SuppressWarnings("PMD.ConfusingTernary")
  // this warning is caused by the call to queryParams(), 
  // but we really don't need the result
  @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
  @Override
  public void before(final Request the_request, final Response the_response) {
    reset();
    my_log_entries.set(new ArrayList<LogEntry>());
    Main.LOGGER.log(logLevel(), 
                    "endpoint " + endpointName() + " hit by " + the_request.host());
    // make sure we get all the HTTP post parameters, if there are any, before
    // anything has a chance to read the request body before Spark
    the_request.queryParams();
    
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
   * The main body of the endpoint. This method wraps an execution of the
   * endpointBody() method of a child class in a way such that unexpected
   * exceptions will be properly logged rather than causing thread deaths.
   * 
   * @param the_request The request.
   * @param the_response The response.
   * @return the result of the endpoint execution.
   */
  // these exception warnings are suppressed because this method is, exactly,
  // attempting to suppress and log all non-Error exceptions other than 
  // HaltException
  @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.AvoidRethrowingException"})
  public final String endpoint(final Request the_request, final Response the_response) {
    String result = null;
    
    try {
      result = endpointBody(the_request, the_response);
    } catch (final HaltException e) {
      // a HaltException should just be propagated, as that is an expected exception
      // that is properly dealt with by Spark
      throw e;
    } catch (final HibernateException e) {
      // a Hibernate exception is treated like a transaction failure
      Main.LOGGER.error("JDBC error in endpoint " + endpointName() + ":\n" + 
                        ExceptionUtils.getStackTrace(e));
      transactionFailure(the_response, e.toString());
      // must manually halt after a transaction failure
      halt(the_response);
    } catch (final Exception e) {
      // some exception occurred that was not handled within the endpoint, so
      // handle it as a generic server error and log the stack trace
      Main.LOGGER.error("uncaught exception in endpoint " + endpointName() + ":\n" + 
                        ExceptionUtils.getStackTrace(e));
      serverError(the_response, e.toString());
      // the server error halts processing
    }
    
    return result;
  }
  
  /**
   * The main body of the endpoint to be executed in child classes.
   * 
   * @param the_request The request
   * @param the_response The response.
   * @return the result of the endpoint execution.
   */
  protected abstract String endpointBody(Request the_request, Response the_response);
  
  /**
   * The after filter for this endpoint. Currently, the implementation is empty.
   */
  public void after(final Request the_request, final Response the_response) {
    // skip
  }
  
  /**
   * Persists, and logs to the system logger, all accumulated log entries for
   * this endpoint.
   * 
   * @param the_request The request (used to get the hostname of the client 
   * and the authentication data for the log).
   */
  private void sendToLogger(final LogEntry the_log_entry) {
    if (the_log_entry.resultCode() == null) {
      Main.LOGGER.log(logLevel(), 
                      the_log_entry.information() + " by " + 
                      the_log_entry.authenticationData() + " from " + 
                      the_log_entry.clientHost());
    } else if (HttpStatus.isSuccess(the_log_entry.resultCode())) {
      Main.LOGGER.log(logLevel(), 
                      "successful " + the_log_entry.information() + " by " + 
                      the_log_entry.authenticationData() + " from " + 
                      the_log_entry.clientHost());
    } else {
      Main.LOGGER.error("error " + the_log_entry.resultCode() + " " + 
                        the_log_entry.information() + " by " + 
                        the_log_entry.authenticationData() + " from " + 
                        the_log_entry.clientHost());
    }
  }

  private void persistLogEntries(final Request the_request) {
    LogEntry previous_entry = LogEntryQueries.last();
    final Object admin_attribute = 
        the_request.session().attribute(AuthenticationInterface.ADMIN);
    final String admin_data;
    if (admin_attribute instanceof Administrator) {
      admin_data = ((Administrator) admin_attribute).username();
    } else {
      admin_data = "(unauthenticated)";
    }
    
    for (final LogEntry entry : my_log_entries.get()) {
      // create and persist a new hash-chained log entry for each log entry
      final LogEntry real_entry =
          new LogEntry(entry.resultCode(), entry.information(), 
                       admin_data, the_request.host(),
                       entry.timestamp(), previous_entry);
      Persistence.save(real_entry);
      sendToLogger(real_entry);
      previous_entry = real_entry;
    }
  }
  
  /**
   * @returns true if the current set of log entries indicates a successful
   * request, false otherwise.
   */
  private boolean successful() {
    return !my_log_entries.get().isEmpty() &&
           HttpStatus.isSuccess(my_log_entries.
                                get().get(my_log_entries.get().size() - 1).resultCode());
  }
  
  /**
   * Attempts to commit, in a separate transaction, any straggling log entries that
   * could not be committed due to error.
   * 
   * @param the_request The request (used for log data).
   */
  private void finalizeLogs(final Request the_request) {
    int log_commit_retries = 0;
    if (!my_log_entries.get().isEmpty() && log_commit_retries < LOG_COMMIT_RETRIES) {
      try {
        log_commit_retries = log_commit_retries + 1;
        Persistence.beginTransaction();
        persistLogEntries(the_request);
        Persistence.commitTransaction();
        my_log_entries.get().clear();
      } catch (final PersistenceException e) {
        Main.LOGGER.error("could not persist log entries for error response after " + 
                          log_commit_retries + " attempt(s)");
      }
    } else if (!my_log_entries.get().isEmpty()) {
      Main.LOGGER.error("maximum number of log entry commit attempts reached, aborting");
    }
  }
  
  /**
   * The afterAfter filter for this endpoint. By default, it attempts to commit 
   * any open transaction (this makes writing endpoint code more straightforward,
   * as the vast majority of endpoints will never have to deal with transactions
   * themselves).
   */
  public void afterAfter(final Request the_request, final Response the_response) {
    // try to take the transition for this endpoint in the ASM and save it to the DB
    // note that we do not try to commit when we have an error code in the response
    if (successful() && 
        transitionAndSaveASM(the_response) && 
        Persistence.isTransactionActive()) {
      try {
        // since the transition finished, let's log all the log entries and commit
        persistLogEntries(the_request);
        Persistence.commitTransaction();
        my_log_entries.get().clear();
      } catch (final PersistenceException e) {
        // this is an internal server error because we don't know what didn't
        // get committed
        transactionFailure(the_response, 
                           "could not commit changes to persistent storage");
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
    // if there are still log entries left, we need to persist them and print them
    finalizeLogs(the_request);
    Integer status = my_status.get();
    String endpoint_result = my_endpoint_result.get();
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR_500;
      endpoint_result = 
          Main.GSON.toJson(new Result("server error, no response from endpoint"));
    }
    the_response.body(endpoint_result);
    the_response.status(status);
  }
  
  /**
   * @return the type of authorization required to use this endpoint.
   * The default is NONE.
   */
  @Override
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.NONE;
  }
  
  /**
   * @return the priority level at which the endpoint's activity will be
   * logged. The default is Priority.INFO.
   */
  @Override
  public Level logLevel() {
    return Level.INFO;
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
   * Resets any thread-local state of an endpoint. The default behavior is
   * to do nothing.
   */
  protected void reset() {
    // do nothing
  }
  
  /**
   * Checks to see that the specified request satisfies the specified
   * authorization type.
   * 
   * @param the_request The request.
   * @param the_type The authorization type.
   * @return true if the request is appropriately authorized, false otherwise.
   */
  public static boolean checkAuthorization(final Request the_request, 
                                           final AuthorizationType the_type) {
    boolean result = the_type == AuthorizationType.NONE;
    if (!result) {
      final boolean state;
      final boolean county;
    
      if (Main.authentication().secondFactorAuthenticated(the_request)) {
        final Administrator admin = 
            Main.authentication().authenticatedAdministrator(the_request);
        if (admin == null) {
          state = false; 
          county = false;
        } else { 
          state = 
              Main.authentication().authenticatedAs(the_request, STATE, admin.username());
          county = 
              Main.authentication().authenticatedAs(the_request, COUNTY, admin.username());
        }

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
      }
    }
      
    return result;
  }
}
