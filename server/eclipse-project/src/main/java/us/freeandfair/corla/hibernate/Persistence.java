/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.util.Pair;

/**
 * Manages persistence through Hibernate, and provides several utility methods.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class Persistence {
  /**
   * The "NO SESSION" constant.
   */
  public static final Session NO_SESSION = null;
  
  /**
   * The "NO TRANSACTION" constant.
   */
  public static final Transaction NO_TRANSACTION = null;
  
  /**
   * The system properties.
   */
  private static Properties system_properties;
  
  /**
   * The service registry for Hibernate.
   */
  private static StandardServiceRegistry service_registry;
  
  /**
   * The session factory for Hibernate.
   */
  private static SessionFactory session_factory;
  
  /**
   * A thread-local containing the active session and transaction on this 
   * thread.
   */
  private static ThreadLocal<Pair<Session, Transaction>> transaction_info =
      new ThreadLocal<Pair<Session, Transaction>>();
  
  /**
   * A flag indicating whether persistence has failed to start or not.
   */
  private static boolean failed;
  
  /**
   * Private constructor to prevent instantiation.
   */
  private Persistence() {
    // do nothing
  }
  
  /**
   * @return true if persistence is enabled, false otherwise.
   */
  public static synchronized boolean isEnabled() {
    return !failed && currentSession() != null;
  }
  
  /**
   * Sets the properties for the system.
   * 
   * @param the_properties The properties.
   */
  public static synchronized void setProperties(final Properties the_properties) {
    system_properties = the_properties;
  }
  
  /**
   * @return a new Session to use for persistence, or null if one cannot be created.
   */
  public static synchronized Session currentSession() {
    Session result = NO_SESSION;
    
    if (!failed) {
      if (session_factory == null) {
        setupSessionFactory();
      } 
      
      if (session_factory == null) {
        failed = true;
      } else {
        try {
          result = session_factory.getCurrentSession();
        } catch (final HibernateException e) {
          Main.LOGGER.info("Exception getting Hibernate session: " + e);
          failed = true;
        }
      }
    }
    
    return result;
  }
  
  /**
   * Sets up the session factory from the properties in the properties file.
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private static synchronized void setupSessionFactory() {
    Main.LOGGER.info("attempting to create Hibernate session factory");
    
    try {
      final StandardServiceRegistryBuilder rb = new StandardServiceRegistryBuilder();      
      final Map<String, String> settings = new HashMap<>();
      
      // get settings from our properties file
      settings.put(Environment.DRIVER, system_properties.getProperty("hibernate.driver", ""));
      settings.put(Environment.URL, system_properties.getProperty("hibernate.url", ""));
      settings.put(Environment.USER, system_properties.getProperty("hibernate.user", ""));
      settings.put(Environment.PASS, system_properties.getProperty("hibernate.pass", ""));
      settings.put(Environment.DIALECT, 
                   system_properties.getProperty("hibernate.dialect", ""));
      settings.put(Environment.HBM2DDL_AUTO, "update");
      settings.put(Environment.SHOW_SQL, "false");
      settings.put(Environment.PHYSICAL_NAMING_STRATEGY, 
                   "us.freeandfair.corla.hibernate.FreeAndFairNamingStrategy");
      settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
      settings.put(Environment.USE_STREAMS_FOR_BINARY, "true");
      
      // apply settings
      rb.applySettings(settings);
      
      // create registry
      service_registry = rb.build();
      
      // create metadata sources and metadata
      final MetadataSources sources = new MetadataSources(service_registry);
      sources.addAnnotatedClass(BallotManifestInfo.class);
      sources.addAnnotatedClass(CastVoteRecord.class);
      sources.addAnnotatedClass(Choice.class);
      sources.addAnnotatedClass(Contest.class);
      sources.addAnnotatedClass(CVRContestInfo.class);
      sources.addAnnotatedClass(Elector.class);
      sources.addAnnotatedClass(UploadedFile.class);
      final Metadata metadata = sources.getMetadataBuilder().build();
      
      // create session factory
      session_factory = metadata.getSessionFactoryBuilder().build();
      Main.LOGGER.info("started Hibernate");
    } catch (final Exception e) {
      Main.LOGGER.info("could not start Hibernate, persistence is disabled: " + e);
      if (service_registry != null) {
        StandardServiceRegistryBuilder.destroy(service_registry);
      }
    }
  }

  /**
   * @return true if a long-lived transaction is running in this thread, 
   * false otherwise.
   */
  public static synchronized boolean isTransactionRunning() {
    final Pair<Session, Transaction> session_transaction = transaction_info.get();
   
    return session_transaction != null && 
           session_transaction.getFirst().equals(Persistence.currentSession()) &&
           session_transaction.getSecond().isActive();
  }
  
  /**
   * Begins a long-lived transaction in this thread that will span several 
   * operations. If such a transaction is running, other persistence
   * operations (such as saveEntity, removeEntity, etc.) will occur within it. 
   * If a long-lived transaction is already running, it is returned by this
   * method.
   * 
   * @return true if a new transaction is started, false if a transaction was
   * already running.
   * @exception HibernateException if a transaction cannot be started or
   * continued.
   */
  public static synchronized boolean beginTransaction() 
      throws HibernateException {
    boolean result = true;
    
    if (Persistence.isEnabled()) {
      Pair<Session, Transaction> session_transaction = transaction_info.get();
      final Session session = Persistence.currentSession(); 
      
      if (session_transaction == null ||
          !session_transaction.getFirst().equals(session) ||
          !session_transaction.getSecond().isActive()) {
        // there was no existing session/transaction pair saved, or the session
        // didn't match the current session, or the transaction had ended already
        session_transaction = 
            new Pair<Session, Transaction>(session, 
                                           session.beginTransaction());
        transaction_info.set(session_transaction);
      } else {
        result = false;
      }
    }
    
    return result;
  }
  
  /**
   * Commits a long-lived transaction.
   * 
   * @exception IllegalStateException if no such transaction is running.
   * @exception HibernateException if there is a problem committing the
   * transaction.
   * @exception RollbackException if the commit fails.
   */
  public static synchronized void commitTransaction() 
      throws IllegalStateException, HibernateException, RollbackException { 
    if (Persistence.isEnabled()) {
      final Pair<Session, Transaction> session_transaction = transaction_info.get();
     
      if (session_transaction == null ||
          !session_transaction.getFirst().equals(Persistence.currentSession())) {
        // there was no existing session/transaction pair saved, or the session
        // didn't match the current session
        transaction_info.set(null);
        throw new IllegalStateException("Attempted to commit nonexistent transaction.");
      } else {
        session_transaction.getSecond().commit();
        transaction_info.set(null);
      }
    }
  }
  
  /**
   * Rolls back a long lived transaction.
   * 
   * @exception IllegalStateException if no such transaction is running.
   * @exception PersistenceException if there is a problem rolling back the 
   * transaction.
   */
  public static synchronized void rollbackTransaction() 
      throws IllegalStateException, PersistenceException {
    if (isEnabled()) {
      final Pair<Session, Transaction> session_transaction = transaction_info.get();
      
      if (session_transaction == null || 
          !session_transaction.getFirst().equals(currentSession())) {
        // there was no existing session/transaction pair saved, or the session
        // didn't match the current session
        transaction_info.set(null);
        throw new IllegalStateException("Attempted to commit nonexistent transaction.");
      } else {
        session_transaction.getSecond().rollback();
        transaction_info.set(null);
      }
    }
  }
}

