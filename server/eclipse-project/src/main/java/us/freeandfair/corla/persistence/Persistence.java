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

package us.freeandfair.corla.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.util.Pair;

/**
 * Manages persistence through Hibernate, and provides several utility methods.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
// we actually do need all these imports to run the persistence subsystem
@SuppressWarnings("PMD.ExcessiveImports")
public final class Persistence {
  /**
   * The path to the resource containing the list of entity classes.
   */
  public static final String ENTITY_CLASSES = 
      "us/freeandfair/corla/persistence/entity_classes";
  
  /**
   * The "NO SESSION" constant.
   */
  public static final Session NO_SESSION = null;
  
  /**
   * The "NO TRANSACTION" constant.
   */
  public static final Transaction NO_TRANSACTION = null;
  
  /**
   * The "no database" string.
   */
  private static final String NO_DATABASE = "no database";
  
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
   * @return true if database persistence is enabled, false otherwise.
   */
  public static synchronized boolean hasDB() {
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
          Main.LOGGER.error("Exception getting Hibernate session: " + e);
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
      
      // database settings
      settings.put(Environment.DRIVER, system_properties.getProperty("hibernate.driver", ""));
      settings.put(Environment.URL, system_properties.getProperty("hibernate.url", ""));
      settings.put(Environment.USER, system_properties.getProperty("hibernate.user", ""));
      settings.put(Environment.PASS, system_properties.getProperty("hibernate.pass", ""));
      settings.put(Environment.DIALECT, 
                   system_properties.getProperty("hibernate.dialect", ""));
      settings.put(Environment.STATEMENT_BATCH_SIZE, "10");
      
      // automatic schema generation
      settings.put(Environment.HBM2DDL_AUTO, 
                   system_properties.getProperty("hibernate.hbm2ddl.auto", ""));
      
      // sql debugging
      settings.put(Environment.SHOW_SQL, 
                   system_properties.getProperty("hibernate.show_sql", ""));
      
      // table and column naming
      settings.put(Environment.PHYSICAL_NAMING_STRATEGY, 
                   "us.freeandfair.corla.persistence.FreeAndFairNamingStrategy");
      
      // concurrency and isolation
      settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
      settings.put(Environment.USE_STREAMS_FOR_BINARY, "true");
      settings.put(Environment.ISOLATION, "SERIALIZABLE");
      
      // caching
      settings.put(Environment.CACHE_PROVIDER_CONFIG, "org.hibernate.cache.EhCacheProvider");
      settings.put(Environment.CACHE_REGION_FACTORY, 
                   "org.hibernate.cache.ehcache.EhCacheRegionFactory");
      settings.put(Environment.USE_SECOND_LEVEL_CACHE, "true");
      settings.put(Environment.USE_QUERY_CACHE, "false");
      settings.put(Environment.DEFAULT_CACHE_CONCURRENCY_STRATEGY, "transactional");
      
      // apply settings
      rb.applySettings(settings);
      
      // create registry
      service_registry = rb.build();
      
      // create metadata sources and metadata
      final MetadataSources sources = new MetadataSources(service_registry);
      try (InputStream entity_stream = 
               ClassLoader.getSystemResourceAsStream(ENTITY_CLASSES)) {
        if (entity_stream == null) {
          Main.LOGGER.error("could not load list of entity classes");          
        } else {
          final Scanner scanner = new Scanner(entity_stream, "UTF-8");
          while (scanner.hasNextLine()) {
            final String entity_class = scanner.nextLine();
            try {
              sources.addAnnotatedClass(Class.forName(entity_class));
            } catch (final ClassNotFoundException e) {
              Main.LOGGER.error("could not add entity, no such class: " + entity_class);
            }
            Main.LOGGER.debug("added entity class " + entity_class);
          }
          scanner.close();
        }
      } catch (final IOException e) {
        Main.LOGGER.error("error reading list of entity classes: " + e);
      } 
      final Metadata metadata = sources.getMetadataBuilder().build();
      
      // create session factory
      session_factory = metadata.getSessionFactoryBuilder().build();
      Main.LOGGER.debug("started Hibernate");
    } catch (final RuntimeException e) {
      Main.LOGGER.error("could not start Hibernate, persistence is disabled: " + e);
      if (service_registry != null) {
        StandardServiceRegistryBuilder.destroy(service_registry);
      }
    }
  }

  /**
   * @return true if a long-lived transaction is running in this thread, 
   * false otherwise.
   * @exception PersistenceException if the database isn't running.
   */
  public static boolean isTransactionActive() 
      throws PersistenceException {
    if (hasDB()) {
      final Pair<Session, Transaction> session_transaction = transaction_info.get();

      return session_transaction != null && 
             session_transaction.getFirst().equals(Persistence.currentSession()) &&
             session_transaction.getSecond().getStatus() == TransactionStatus.ACTIVE;
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
  }
  
  /**
   * @return true if a long-lived transaction can be rolled back,
   * false otherwise.
   * @exception PersistenceException if the database isn't running.
   */
  public static boolean canTransactionRollback()
      throws PersistenceException {
    if (hasDB()) {
      final Pair<Session, Transaction> session_transaction = transaction_info.get();

      return session_transaction != null && 
             session_transaction.getFirst().equals(Persistence.currentSession()) &&
             session_transaction.getSecond().getStatus().canRollback();
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
  }
  
  /**
   * Begins a long-lived transaction in this thread that will span several 
   * operations. If such a transaction is running, other persistence
   * operations (such as saveEntity, removeEntity, etc.) will occur within it. 
   * 
   * @return true if a new transaction is started, false if a transaction was
   * already running.
   * @exception PersistenceException if a transaction cannot be started or
   * continued.
   */
  public static boolean beginTransaction() 
      throws PersistenceException {    
    if (hasDB()) {
      boolean result = true;
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
      
      return result;
    } else {
      throw new PersistenceException(NO_DATABASE);
    }    
  }
  
  /**
   * Commits the active long-lived transaction.
   * 
   * @exception IllegalStateException if no such transaction is running.
   * @exception PersistenceException if there is a problem with persistent storage.
   * @exception RollbackException if the commit fails.
   */
  public static void commitTransaction() 
      throws IllegalStateException, PersistenceException, RollbackException { 
    if (hasDB()) {
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
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
  }
  
  /**
   * Rolls back the active long lived transaction.
   * 
   * @exception IllegalStateException if no such transaction is running.
   * @exception PersistenceException if there is a problem with persistent storage.
   */
  public static void rollbackTransaction() 
      throws IllegalStateException, PersistenceException {
    if (hasDB()) {
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
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
  }
  
  /**
   * Saves or updates the specified object in persistent storage. This is done
   * in the currently open transaction, if one exists; otherwise, it starts and
   * commits one for this operation.
   * 
   * @param the_object The object to save or update.
   * @return true if the save/update was successful, false otherwise
   * @exception PersistenceException if the database isn't running.
   */
  public static boolean saveOrUpdate(final PersistentEntity the_object) 
      throws PersistenceException {    
    if (hasDB()) {
      boolean result = true;

      try {
        final boolean transaction = beginTransaction();
        currentSession().saveOrUpdate(the_object);
        if (transaction) {
          try {
            commitTransaction();
          } catch (final RollbackException e) {
            rollbackTransaction();
            Main.LOGGER.debug("could not save/update object " + the_object + ": " + e);
            result = false;
          }
        }
      } catch (final PersistenceException e) {
        Main.LOGGER.debug("could not save/update object " + the_object + ": " + e);
        result = false;
      }
      
      return result;
    } else {
      throw new PersistenceException(NO_DATABASE);
    }    
  }
  
  /**
   * Deletes the specified object from persistent storage, if it exists.
   * 
   * @param the_object The object to delete.
   * @return true if the deletion was successful, false otherwise (if 
   * the object did not exist, false is returned).
   * @exception PersistenceException if the database isn't running.
   */
  public static boolean delete(final PersistentEntity the_object) 
      throws PersistenceException {    
    if (hasDB()) {
      boolean result = true;

      try {
        final boolean transaction = beginTransaction();
        currentSession().delete(the_object);
        if (transaction) {
          try {
            commitTransaction();
          } catch (final RollbackException e) {
            rollbackTransaction();
            Main.LOGGER.debug("could not delete object " + the_object + ": " + e);
            result = false;
          }
        }
      } catch (final PersistenceException e) {
        result = false;
        Main.LOGGER.debug("could not delete object " + the_object + ": " + e);
      }
      
      return result;
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
  }
  
  /**
   * Deletes the object of the specified class with the specified ID from
   * persistent storage, if it exists.
   * 
   * @param the_class The class of the object to delete.
   * @param the_id The ID of the object to delete.
   * @return true if the deletion was successful, false otherwise (if 
   * the object did not exist, false is returned).
   * @exception PersistenceException if the database isn't running.
   */
  public static boolean delete(final Class<? extends PersistentEntity> the_class,
                               final Long the_id) 
      throws PersistenceException {    
    if (hasDB()) {
      boolean result = true;
      
      try {
        final boolean transaction = beginTransaction();
        final PersistentEntity instance = currentSession().load(the_class, the_id);
        if (instance == null) {
          result = false;
        } else {
          currentSession().delete(instance);
        }
        if (transaction) {
          try {
            commitTransaction();
          } catch (final RollbackException | HibernateException e) {
            rollbackTransaction();
            Main.LOGGER.debug("error deleting object of class " + the_class + 
                              "with ID " + the_id + ": " + e);
            result = false;
          }
        }
      } catch (final PersistenceException e) {
        Main.LOGGER.debug("error deleting object of class " + the_class + 
                          "with ID " + the_id + ": " + e);
        result = false;
      }
      
      return result;
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
  }
  
  /**
   * Gets the entity in the current session that has the specified ID and class.
   * 
   * @param the_id The ID.
   * @param the_class The class.
   * @return the result entity, or null if no such entity exists.
   * @exception PersistenceException if the database isn't running.
   */
  public static <T extends PersistentEntity> T getByID(final Serializable the_id, 
                                                       final Class<T> the_class) 
      throws PersistenceException {
    if (hasDB()) {
      T result = null;
      boolean transaction = false;
      Main.LOGGER.debug("searching session for object " + the_class + "/" + the_id);
      try {
        transaction = beginTransaction();
        result = currentSession().get(the_class, the_id);
        if (transaction) {
          try {
            commitTransaction();
          } catch (final RollbackException e) {
            rollbackTransaction();
          }
        }
      } catch (final PersistenceException e) {
        Main.LOGGER.error("exception when searching for " + the_class + "/" + the_id + 
                          ": " + e);
      }
      return result;
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
  }
  
  /**
   * Gets all the entities of the specified class.
   * 
   * @param the_class The class.
   * @return a list containing all the entities of the_class.
   * @exception PersistenceException if the database isn't running.
   */
  // TODO: make streaming or iterable
  public static <T extends PersistentEntity> List<T> getAll(final Class<T> the_class) 
      throws PersistenceException {
    final List<T> result = new ArrayList<>();
    
    if (hasDB()) {
      try {
        final boolean transaction = Persistence.beginTransaction();
        final Session s = Persistence.currentSession();
        final CriteriaBuilder cb = s.getCriteriaBuilder();
        final CriteriaQuery<T> cq = cb.createQuery(the_class);
        final Root<T> root = cq.from(the_class);
        cq.select(root);
        final TypedQuery<T> query = s.createQuery(cq);
        result.addAll(query.getResultList());
        if (transaction) {
          try {
            Persistence.commitTransaction();
          } catch (final RollbackException e) {
            Persistence.rollbackTransaction();
          }
        }
      } catch (final PersistenceException e) {
        Main.LOGGER.error("could not query database");
      }
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
    
    return result;
  }
  
  /**
   * Gets a stream of all the entities of the specified class. This method 
   * <em>must</em> be called from within a transaction, and the result stream 
   * must be used within the same transaction.
   * 
   * @param the_class The class.
   * @return a stream containing all the entities of the_class, or null if
   * one could not be acquired.
   * @exception PersistenceException if the database isn't running.
   * @exception IllegalStateException if no transaction is running.
   */
  public static <T extends PersistentEntity> Stream<T> 
      getAllAsStream(final Class<T> the_class) throws PersistenceException {
    if (!isTransactionActive()) {
      throw new IllegalStateException("no running transaction");
    }
   
    Stream<T> result = null;
    
    if (hasDB()) {
      try {
        final Session s = Persistence.currentSession();
        final CriteriaBuilder cb = s.getCriteriaBuilder();
        final CriteriaQuery<T> cq = cb.createQuery(the_class);
        final Root<T> root = cq.from(the_class);
        cq.select(root);
        final Query<T> query = s.createQuery(cq);
        result = query.stream();
      } catch (final PersistenceException e) {
        Main.LOGGER.error("could not query database");
      }
    } else {
      throw new PersistenceException(NO_DATABASE);
    }
    
    return result;
  }
}

