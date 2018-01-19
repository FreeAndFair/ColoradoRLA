/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 27, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
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

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
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

/**
 * Manages persistence through Hibernate, and provides several utility methods.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass"})
public final class Persistence {
  /**
   * The path to the resource containing the list of entity classes.
   */
  public static final String ENTITY_CLASSES = 
      "us/freeandfair/corla/persistence/entity_classes";
  
  /**
   * The "true" constant.
   */
  public static final String TRUE = "true";
  
  /**
   * The "false" constant.
   */
  public static final String FALSE = "false";
  
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
   * A thread-local containing the active session on this thread.
   */
  private static ThreadLocal<Session> session_info = new ThreadLocal<Session>();
  
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
    return !failed && (session_info.get() != null || openSession() != null);
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
   * Creates a new Session. Note that this method should typically only be called 
   * by the Persistence methods that control transactions.
   * 
   * @return the new Session.
   * @exception IllegalStateException if a Session is already open on this thread.
   */
  public static synchronized Session openSession() {
    Session session = session_info.get();
    if (session != null && session.isOpen()) {
      throw new IllegalStateException("session is already open on this thread");
    }
    
    if (!failed && session == null) {
      if (session_factory == null) {
        setupSessionFactory();
      } 
      
      if (session_factory == null) {
        failed = true;
      } else {
        try {
          session = session_factory.openSession();
          session_info.set(session);
        } catch (final HibernateException e) {
          Main.LOGGER.error("Exception getting Hibernate session: " + e);
          failed = true;
        }
      }
    }
    return session;
  }
  
  /**
   * @return the currently open session.
   * @exception PersistenceException if there is no currently open session.
   */
  public static Session currentSession() {
    final Session session = session_info.get();
    if (session == null || !session.isOpen()) {
      throw new PersistenceException("no open session");
    } else {
      return session;
    }
  }
  
  /**
   * Sets up the session factory from the properties in the properties file.
   */
  @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.ExcessiveMethodLength",
                     "checkstyle:magicnumber", "checkstyle:executablestatementcount",
                     "checkstyle:methodlength"})
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
      
      // C3P0 connection pooling
      settings.put(Environment.C3P0_MIN_SIZE, 
                   system_properties.getProperty("hibernate.c3p0.min_size", "20"));
      settings.put(Environment.C3P0_MAX_SIZE, 
                   system_properties.getProperty("hibernate.c3p0.max_size", "20"));
      settings.put(Environment.C3P0_IDLE_TEST_PERIOD,
                   system_properties.getProperty("hibernate.c3p0.idle_test_period", "0"));
      settings.put(Environment.C3P0_MAX_STATEMENTS, 
                   system_properties.getProperty("hibernate.c3p0.max_statements", "0"));
      settings.put(Environment.C3P0_TIMEOUT, 
                   system_properties.getProperty("hibernate.c3p0.timeout", "300"));
      settings.put("hibernate.c3p0.numHelperThreads", 
                   system_properties.getProperty("hibernate.c3p0.numHelperThreads", "3"));
      settings.put("hibernate.c3p0.privilegeSpawnedThreads", TRUE);
      settings.put("hibernate.c3p0.contextClassLoaderSource", "none");
      
      // automatic schema generation
      settings.put(Environment.HBM2DDL_AUTO, 
                   system_properties.getProperty("hibernate.hbm2ddl.auto", ""));
      
      // sql debugging
      settings.put(Environment.SHOW_SQL, 
                   system_properties.getProperty("hibernate.show_sql", FALSE));
      settings.put(Environment.FORMAT_SQL, 
                   system_properties.getProperty("hibernate.format_sql", FALSE));
      settings.put(Environment.USE_SQL_COMMENTS, 
                   system_properties.getProperty("hibernate.use_sql_comments", FALSE));
      
      // table and column naming
      settings.put(Environment.PHYSICAL_NAMING_STRATEGY, 
                   "us.freeandfair.corla.persistence.FreeAndFairNamingStrategy");
      
      // concurrency and isolation
      settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
      settings.put(Environment.USE_STREAMS_FOR_BINARY, TRUE);
      settings.put(Environment.AUTOCOMMIT, FALSE);
      settings.put(Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, TRUE);
      settings.put(Environment.ISOLATION, "REPEATABLE_READ");
      
      // caching 
      settings.put(Environment.JPA_SHARED_CACHE_MODE, "ENABLE_SELECTIVE");
      settings.put(Environment.CACHE_PROVIDER_CONFIG, "org.hibernate.cache.EhCacheProvider");
      settings.put(Environment.CACHE_REGION_FACTORY, 
                   "org.hibernate.cache.ehcache.EhCacheRegionFactory");
      settings.put(Environment.USE_SECOND_LEVEL_CACHE, FALSE);
      settings.put(Environment.USE_QUERY_CACHE, FALSE);
      // IMPORTANT: the USE_DIRECT_REFERENCE_CACHE_ENTRIES setting is FALSE to address
      // Hibernate bug HHH-11169, and must not be changed until/unless that bug is 
      // resolved
      settings.put(Environment.USE_DIRECT_REFERENCE_CACHE_ENTRIES, FALSE);
      settings.put(Environment.DEFAULT_CACHE_CONCURRENCY_STRATEGY, "read-write"); 
      
      // other performance
      settings.put(Environment.ORDER_INSERTS, TRUE);
      settings.put(Environment.ORDER_UPDATES, TRUE);
      settings.put(Environment.BATCH_VERSIONED_DATA, TRUE);
      settings.put(Environment.STATEMENT_BATCH_SIZE, "100");
      settings.put(Environment.BATCH_FETCH_STYLE, "DYNAMIC");
      settings.put(Environment.VALIDATE_QUERY_PARAMETERS, FALSE);
      settings.put(Environment.DEFAULT_BATCH_FETCH_SIZE, "16");
      settings.put(Environment.MAX_FETCH_DEPTH, "3");
      
      // empty composite objects
      settings.put(Environment.CREATE_EMPTY_COMPOSITES_ENABLED, TRUE);
      
      // statistics
      settings.put(Environment.GENERATE_STATISTICS, FALSE);
      
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
   * @return true if a session is open on this thread, false otherwise.
   * @exception IllegalStateException if the database isn't running.
   */
  public static boolean isSessionOpen() 
      throws PersistenceException {
    checkForDatabase();
    
    final Session session = session_info.get();
    return session != null && session.isOpen();
  }
  
  /**
   * @return true if a long-lived transaction is running in this thread, 
   * false otherwise.
   * @exception IllegalStateException if the database isn't running.
   */
  public static boolean isTransactionActive() 
      throws PersistenceException {
    checkForDatabase();

    final Session session = session_info.get();
    Transaction transaction = null;
    if (session != null) {
      try {
        transaction = session.getTransaction();
      } catch (final HibernateException e) {
        // the session did not have a transaction
      }
    }
    return session != null && transaction != null &&
           transaction.getStatus() == TransactionStatus.ACTIVE;
  }
  
  /**
   * @return true if a long-lived transaction can be rolled back,
   * false otherwise.
   * @exception IllegalStateException if the database isn't running.
   */
  public static boolean canTransactionRollback()
      throws PersistenceException {
    checkForDatabase();

    final Session session = currentSession();
    Transaction transaction = null;
    try {
      transaction = session.getTransaction();
    } catch (final HibernateException e) {
      // the session did not have a transaction
    }
    return transaction != null && transaction.getStatus().canRollback();
  }
  
  /**
   * Begins a long-lived transaction in this thread that will span several 
   * operations, opening a session if necessary. If an existing transaction 
   * is in a non-active state, it is rolled back (if possible) and a new transaction 
   * is started. 
   * 
   * @return true if a new transaction is started, false if a transaction was
   * already active.
   * @exception IllegalStateException if the database isn't running.
   * @exception PersistenceException if a transaction cannot be started or
   * continued.
   */
  public static boolean beginTransaction() 
      throws PersistenceException {
    checkForDatabase();

    boolean result = true;
    Session session = currentSession();
    
    if (isTransactionActive()) {
      result = false;
    } else if (canTransactionRollback()) {
      rollbackTransaction();
      // session is explicitly closed by rollback
      // interesting note: isOpen() on the session _still returns true_ even though 
      // it is closed!
      session = openSession();
      session.beginTransaction();
    } else {
      // we don't have an active or rollback-able transaction, so we just
      // start a new one
      session.beginTransaction();
    } 

    return result; 
  }
  
  /**
   * Commits the active long-lived transaction. This also closes the current 
   * session, regardless of the transaction's success (it is rolled back if 
   * it does not succeed).
   * 
   * @exception IllegalStateException if no such transaction is running.
   * @exception PersistenceException if there is a problem with persistent storage.
   * @exception RollbackException if the commit fails.
   */
  public static void commitTransaction() 
      throws IllegalStateException, PersistenceException, RollbackException {
    checkForRunningTransaction();
    try {
      currentSession().getTransaction().commit();
    } finally {
      currentSession().close();
      session_info.remove();
    }
    
  }
  
  /**
   * Rolls back the active long lived transaction. This also closes the current
   * session, regardless of the rollback's success.
   * 
   * @exception IllegalStateException if no such transaction is running, or if the
   * running transaction cannot be rolled back.
   * @exception PersistenceException if there is a problem with persistent storage.
   */
  public static void rollbackTransaction() 
      throws IllegalStateException, PersistenceException {
    if (canTransactionRollback()) {
      try {
        currentSession().getTransaction().rollback();
      } finally {
        currentSession().close();
        session_info.remove();
      }
    } else {
      throw new IllegalStateException("no active transaction to roll back");
    }
  }
  
  /**
   * Saves or updates the specified object in persistent storage. This
   * method must be called within a transaction.
   * 
   * @param the_object The object to save or update.
   * @return true if the save/update was successful, false otherwise
   * @exception IllegalStateException if no database is available or no 
   * transaction is running.
   */
  public static boolean saveOrUpdate(final PersistentEntity the_object) 
      throws IllegalStateException {    
    checkForRunningTransaction();   

    boolean result = true;

    try {
      currentSession().saveOrUpdate(the_object);
    } catch (final PersistenceException e) {
      Main.LOGGER.debug("could not save/update object " + the_object + ": " + e);
      result = false;
    }

    return result;
  }
  
  /**
   * Saves the specified object in persistent storage. This will cause an 
   * exception if there is already an object in persistent storage with the same
   * class and ID. This method must be called within a transaction.
   * 
   * @param the_object The object to save.
   * @exception IllegalStateException if no database is available or no 
   * transaction is running.
   * @exception PersistenceException if the object cannot be saved.
   */
  public static void save(final PersistentEntity the_object) 
      throws IllegalStateException, PersistenceException {
    checkForRunningTransaction();
    currentSession().save(the_object);
  }
  
  /**
   * Updates the specified object in persistent storage. This will cause an
   * exception if there is no object in persistent storage with the same class
   * and ID. This method must be called within a transaction.
   * 
   * @param the_object The object to save.
   * @exception IllegalStateException if no database is available or no
   * transaction is running.
   * @exception PersistenceException if the object cannot be updated.
   */
  public static void update(final PersistentEntity the_object)
      throws IllegalStateException, PersistenceException {
    checkForRunningTransaction();
    currentSession().update(the_object);
  }
  
  /**
   * Deletes the specified object from persistent storage, if it exists. This
   * method must be called within a transaction.
   * 
   * @param the_object The object to delete.
   * @return true if the deletion was successful, false otherwise (if 
   * the object did not exist, false is returned).
   * @exception IllegalStateException if no database is available or no 
   * transaction is running.
   */
  public static boolean delete(final PersistentEntity the_object) 
      throws IllegalStateException {    
    checkForRunningTransaction();   
   
    boolean result = true;

    try {
      currentSession().delete(the_object);
    } catch (final PersistenceException e) {
      result = false;
      Main.LOGGER.debug("could not delete object " + the_object + ": " + e);
    }
      
    return result;
  }
  
  /**
   * Deletes the object of the specified class with the specified ID from
   * persistent storage, if it exists. This method must be called within
   * a transaction.
   * 
   * @param the_class The class of the object to delete.
   * @param the_id The ID of the object to delete.
   * @return true if the deletion was successful, false otherwise (if 
   * the object did not exist, false is returned).
   * @exception IllegalStateException if no database is available or no 
   * transaction is running.
   */
  public static boolean delete(final Class<? extends PersistentEntity> the_class,
                               final Long the_id) 
      throws IllegalStateException {    
    checkForRunningTransaction();   

    boolean result = true;

    try {
      final PersistentEntity instance = currentSession().load(the_class, the_id);
      if (instance == null) {
        result = false;
      } else {
        try {
          currentSession().delete(instance);
        } catch (final ObjectNotFoundException e) {
          // no object with this ID to delete
          result = false;
        }
      }
    } catch (final PersistenceException e) {
      Main.LOGGER.debug("error deleting object of class " + the_class + 
                        "with ID " + the_id + ": " + e);
      result = false;
    }

    return result;
  }
  
  /**
   * Gets the entity in the current session that has the specified ID and class.
   * This method must be called within a transaction.
   * 
   * @param the_id The ID.
   * @param the_class The class.
   * @return the result entity, or null if no such entity exists.
   * @exception IllegalStateException if no database is available or no 
   * transaction is running.
   */
  public static <T extends PersistentEntity> T getByID(final Serializable the_id, 
                                                       final Class<T> the_class) 
      throws IllegalStateException {
    checkForRunningTransaction();   

    T result = null;
    Main.LOGGER.debug("searching session for object " + the_class + "/" + the_id);
    try {
      result = currentSession().get(the_class, the_id);
    } catch (final PersistenceException e) {
      Main.LOGGER.error("exception when searching for " + the_class + "/" + the_id + 
                        ": " + e);
    }
    return result;
  } 
  
  /**
   * Gets all the entities of the specified class. This method must be called
   * within a transaction.
   * 
   * @param the_class The class.
   * @return a list containing all the entities of the_class.
   * @exception IllegalStateException if no database is available or no 
   * transaction is running.
   */
  public static <T extends PersistentEntity> List<T> getAll(final Class<T> the_class) 
      throws IllegalStateException {
    checkForRunningTransaction();   
    
    final List<T> result = new ArrayList<>();
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<T> cq = cb.createQuery(the_class);
      final Root<T> root = cq.from(the_class);
      cq.select(root);
      final TypedQuery<T> query = s.createQuery(cq);
      result.addAll(query.getResultList());
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database");
    }

    return result;
  }
  
  /**
   * Gets a stream of all the entities of the specified class. This method 
   * must be called within a transaction, and the result stream must be used 
   * within the same transaction.
   * 
   * @param the_class The class.
   * @return a stream containing all the entities of the_class, or null if
   * one could not be acquired.
   * @exception IllegalStateException if no database is available or no 
   * transaction is running.
   */
  public static <T extends PersistentEntity> Stream<T> 
      getAllAsStream(final Class<T> the_class) throws IllegalStateException {
    checkForRunningTransaction();
    
    Stream<T> result = null;

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
    
    return result;
  }
  
  /**
   * Flushes the current session, if one exists. If no session is open, this
   * method is equivalent to a skip.
   * 
   * @exception PersistenceException if there is a problem flushing the session.
   */
  public static void flush() throws PersistenceException {
    final Session session = session_info.get();
    if (session != null) {
      session.flush();
    }
  }
  
  /**
   * Evicts the specified object from the current session, if one exists. If no
   * session is open, this method is equivalent to a skip.
   * 
   * @exception NullPointerException if a null object is specified.
   * @exception IllegalArgumentException if the specified object is not an entity.
   */
  public static void evict(final PersistentEntity the_entity) {
    final Session session = session_info.get();
    if (session != null) {
      session.evict(the_entity);
    }
  }
  
  /**
   * Clears all entities from the current session, if one exists. This also
   * causes a flush to occur, to ensure that no previous state changes are lost
   * (for a "naked" clear, use currentSession().clear()). If no session is open, 
   * this method is equivalent to a skip.
   * 
   * @exception PersistenceException if there is a problem flushing or clearing
   * the session.
   */
  public static void flushAndClear() {
    final Session session = session_info.get();
    if (session != null) {
      session.flush();
      session.clear();
    }
  }
  
  /**
   * Gets a streaming Blob for the specified input stream and file size. This method
   * must be called within a running transaction.
   * 
   * @param the_stream The input stream.
   * @param the_size The file size.
   * @exception IllegalStateException if there is no running transaction.
   */
  public static Blob blobFor(final InputStream the_stream, final long the_size) {
    checkForRunningTransaction();
    return currentSession().getLobHelper().createBlob(the_stream, the_size);
  }
  
  /**
   * Unwraps an object from its proxy object, if any; typically used before 
   * converting the entity to JSON for wire transmission.
   * 
   * @param the_object The object.
   * @return the unwrapped object; if the object is not a proxy, it is returned
   * unchanged.
   */
  public static Object unproxy(final Object the_object) {
    return Hibernate.unproxy(the_object);
  }
  
  /**
   * Throws an IllegalStateException if there is no running transaction.
   */
  private static void checkForRunningTransaction() throws IllegalStateException {
    if (!isTransactionActive()) {
      throw new IllegalStateException("no running transaction");
    }
  }
  
  /**
   * Throws an IllegalStateException if there is no database.
   */
  private static void checkForDatabase() throws IllegalStateException {
    if (!hasDB()) {
      throw new IllegalStateException("no database");
    }
  }
}
