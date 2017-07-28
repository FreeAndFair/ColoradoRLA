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
import us.freeandfair.corla.model.BallotStyle;
import us.freeandfair.corla.model.CastVoteRecord;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;

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
   * Private constructor to prevent instantiation.
   */
  private Persistence() {
    // do nothing
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
  public static synchronized Session openSession() {
    if (session_factory == null) {
      setupSessionFactory();
    } 
    
    if (session_factory == null) {
      return null;
    } else {
      try {
        return session_factory.openSession();
      } catch (final HibernateException e) {
        Main.LOGGER.info("Exception opening Hibernate session: " + e);
        return null;
      }
    }
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
      settings.put(Environment.SHOW_SQL, "true");
      settings.put(Environment.PHYSICAL_NAMING_STRATEGY, 
                   "us.freeandfair.corla.hibernate.FreeAndFairNamingStrategy");
      
      // apply settings
      rb.applySettings(settings);
      
      // create registry
      service_registry = rb.build();
      
      // create metadata sources and metadata
      final MetadataSources sources = new MetadataSources(service_registry);
      sources.addAnnotatedClass(BallotManifestInfo.class);
      sources.addAnnotatedClass(BallotStyle.class);
      sources.addAnnotatedClass(CastVoteRecord.class);
      sources.addAnnotatedClass(Choice.class);
      sources.addAnnotatedClass(Contest.class);
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
   * Gets an entity in the specified session matching the filled fields of the 
   * specified object, or saves the specified object in the session if there is 
   * no match.
   * 
   * @param the_object The object.
   * @param the_class The class of the object.
   * @param the_session The session.
   * @return the result entity.
   */
  public static <T> T getEntity(final T the_object, final Class<T> the_class, 
                                final Session the_session) {
    /*
    // this is very much work in progress
    if (the_session == null) {
      return the_object;
    }
    final Criteria cr = 
      the_session.createCriteria(the_class).add(Example.create(the_object));
    try {
      T result = (T) cr.uniqueResult();
      if (result == null) {
        the_session.save(the_object);
        result = the_object;
      }
      return result;
    } catch (final HibernateException e) {
      Main.LOGGER.info("exception when searching for object matching " + the_object); 
      e.printStackTrace();
      return the_object;
    }
    */
    return the_object;
  }
}
