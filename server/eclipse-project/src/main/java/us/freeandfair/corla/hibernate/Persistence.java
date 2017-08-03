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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.criterion.Example;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.BallotManifestInfo;
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
  public static synchronized Session getCurrentSession() {
    if (session_factory == null) {
      setupSessionFactory();
    } 
    
    if (session_factory == null) {
      return null;
    } else {
      try {
        return session_factory.getCurrentSession();
      } catch (final HibernateException e) {
        Main.LOGGER.info("Exception getting Hibernate session: " + e);
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
   * Gets a single entity in the current session matching the specified object, 
   * or saves the specified object in the current session if there is no match. 
   * If persistence is not running, the specified object is returned. This method
   * is meant to be used with completely specified entities only, and will neither
   * save the specified object nor return a result from the database if there
   * are multiple matches in the database.
   * 
   * @param the_object The object.
   * @param the_class The class of the object to search for.
   * @return the result entity, or the original object if no result entity can
   * be acquired.
   */
  // the one unchecked cast that Java thinks is in this method is actually 
  // a checked cast
  @SuppressWarnings("unchecked")
  public static <T> T getEntity(final T the_object, final Class<T> the_class) {
    T result = the_object;
    final Session session = getCurrentSession();
    
    if (session != null) {
      // try to use the provided object as an example
      @SuppressWarnings("deprecation") // no query by example in JPA
      final Criteria cr = session.createCriteria(the_class);
      cr.add(Example.create(the_object));
      try {
        final Object match = cr.uniqueResult();
        if (match == null) {
          session.save(the_object); 
        } else if (the_class.isAssignableFrom(match.getClass())) {
          // this is a checked cast even though Java thinks it isn't
          result = (T) match;
        }
      } catch (final HibernateException e) {
        Main.LOGGER.info("exception when searching for object matching " + 
                         the_object + ": " + e);
        result = the_object;
      }
    }
    
    return result;
  }
  
  /**
   * Gets a list of entities in the current session that match the filled
   * fields of the specified object. If persistence is not running, or an
   * error occurs while performing the search, the returned list contains
   * only the specified object.
   * 
   * @param the_object The object.
   * @param the_class The class of the object to search for.
   * @return a list of result entities.
   */
  // the one unchecked cast that Java thinks is in this method is actually
  // a checked cast
  @SuppressWarnings("unchecked")
  public static <T> List<T> getMatchingEntities(final T the_object, 
                                                final Class<T> the_class) {
    final List<T> result = new ArrayList<T>();
    final Session session = getCurrentSession();
    
    if (session == null) {
      result.add(the_object);
    } else {
      // try to use the provided object as an example
      @SuppressWarnings("deprecation") // no query by example in JPA
      final Criteria cr = session.createCriteria(the_class);
      cr.add(Example.create(the_object));
      try {
        final List<?> matches = cr.list();
        for (final Object o : matches) {
          if (the_class.isAssignableFrom(o.getClass())) {
            // this is a checked cast even though Java thinks it isn't
            result.add((T) o);
          }
        }
      } catch (final HibernateException e) {
        Main.LOGGER.info("exception when searching for object matching " + 
                         the_object + ": " + e);
        result.add(the_object);
      }
    }
    
    return result;
  }
  
  public static void main(final String... the_args) {
    // test entity lookup
    
    setProperties(Main.defaultProperties());
    
    Choice c1 = Choice.instance("name1", "description1");
    Choice c2 = Choice.instance("name2", "description2");
    
    Choice e1 = getEntity(c1, Choice.class);
    Choice e2 = getEntity(c2, Choice.class);
    
    Choice e3 = getEntity(Choice.instance("name1", "description1"), Choice.class);
    Choice e4 = getEntity(Choice.instance("name2", "description2"), Choice.class);
    System.err.println(c1.id() + ": " + c1);
    System.err.println(e1.id() + ": " + e1);
    System.err.println(e3.id() + ": " + e3);
    System.err.println(c2.id() + ": " + c2);
    System.err.println(e2.id() + ": " + e2);
    System.err.println(e4.id() + ": " + e4);
    
    
  }
}
