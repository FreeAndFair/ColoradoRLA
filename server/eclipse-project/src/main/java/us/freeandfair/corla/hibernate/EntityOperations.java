/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 7, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import us.freeandfair.corla.Main;

/**
 * Common operations carried out on, or for retrieving, persistent entities.
 * 
 * @author Daniel M. Zimmerman
 * @version 0.0.1
 */
public final class EntityOperations {
  /**
   * Private constructor to prevent instantiation.
   */
  private EntityOperations() {
    // do nothing
  }
  
  /**
   * Saves the specified object as an entity in the current session. If 
   * beginTransaction() was previously used to begin a transaction, the object
   * will not be committed until that transaction is committed; otherwise, 
   * the object is saved in a single isolated transaction.
   * 
   * @return true if the save succeeded, false otherwise.
   */
  public static boolean saveEntity(final Object the_object) {
    boolean result = true;
    boolean transaction = false;
    
    try {
      transaction = Persistence.beginTransaction();
      Persistence.currentSession().saveOrUpdate(the_object);
      if (transaction) {
        Persistence.commitTransaction();
      }
    } catch (final HibernateException e) {
      if (transaction) {
        try {
          Persistence.rollbackTransaction();
        } catch (final IllegalStateException | PersistenceException ex) {
          // ignore
        }
      }
      Main.LOGGER.info("Exception while saving entity " + the_object + ": " + e);
      result = false;
    }
    
    return result;
  }
  
  /** 
   * Removes the specified object from the database.
   * 
   * @return true if the remove succeeded, false otherwise.
   */
  public static boolean removeEntity(final Object the_object) {
    boolean result = true;
    boolean transaction = false;
    
    try {
      transaction = Persistence.beginTransaction();
      Persistence.currentSession().delete(the_object);
      if (transaction) {
        Persistence.commitTransaction();
      }
    } catch (final HibernateException e) {
      if (transaction) {
        try {
          Persistence.rollbackTransaction();
        } catch (final IllegalStateException | PersistenceException ex) {
          // ignore
        }
        Main.LOGGER.info("Exception while forgetting entity " + the_object + ": " + e);
        result = false;
      }     
    }
    
    return result;
  }
  
  /**
   * Removes the specified object from the database, by ID and class.
   * 
   * @param the_id The object ID.
   * @param the_class The class of the object to remove.
   * @return true if the remove succeeded, false otherwise.
   */
  public static boolean removeEntity(final Serializable the_id, final Class<?> the_class) {
    boolean result = true;
    boolean transaction = false;
    
    try {
      transaction = Persistence.beginTransaction();
      final Object o = Persistence.currentSession().load(the_class, the_id);
      Persistence.currentSession().delete(o);
      if (transaction) {
        Persistence.commitTransaction();
      }
    } catch (final HibernateException e) {
      if (transaction) {
        try {
          Persistence.rollbackTransaction();
        } catch (final IllegalStateException | PersistenceException ex) {
          // ignore
        }
        Main.LOGGER.info("Exception while forgetting entity " + the_class + "/" + the_id + 
                         ": " + e);
        result = false;
      }     
    }
    
    return result;    
  }
  
  /**
   * Gets the entity in the current session that has the specified ID and class.
   * 
   * @param the_id The ID.
   * @param the_class The class.
   * @return the result entity, or null if no such entity exists.
   */
  public static <T> T entityByID(final Serializable the_id, final Class<T> the_class) {
    T result = null;
    boolean transaction = false;
    
    try {
      transaction = Persistence.beginTransaction();
      result = Persistence.currentSession().get(the_class, the_id);
      if (transaction) {
        Persistence.commitTransaction();
      }
    } catch (final HibernateException | RollbackException e) {
      if (transaction) {
        try {
          Persistence.rollbackTransaction();
        } catch (final IllegalStateException | PersistenceException ex) {
          // ignore
        }
      }
      Main.LOGGER.info("Exception while attempting to retrieve " + 
                       the_class + ", id " + the_id + ": " + e);
    }
    
    return result;
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
  public static <T> T matchingEntity(final T the_object, final Class<T> the_class) {
    T result = the_object;
    final Session session = Persistence.currentSession();
    boolean transaction = false;
    
    if (session != null) {
      Main.LOGGER.info("searching session for object " + the_object);
      transaction = Persistence.beginTransaction();
      @SuppressWarnings("deprecation") // no query by example in JPA
      final Criteria cr = session.createCriteria(the_class);
      cr.add(Example.create(the_object));
      try {
        final Object match = cr.uniqueResult();
        if (match == null) {
          Main.LOGGER.info("object not found");
          session.saveOrUpdate(result); 
        } else if (match.equals(the_object)) {
          // this is a checked cast even though Java thinks it isn't
          Main.LOGGER.info("object found: " + match);
          result = (T) match;
        } else {
          // we found an object but it didn't match
          Main.LOGGER.info("search returned mismatched object " + match);
          session.saveOrUpdate(result); 
        }
        if (transaction) {
          Persistence.commitTransaction();
        }
      } catch (final HibernateException e) {
        if (transaction) {
          try {
            Persistence.rollbackTransaction();
          } catch (final IllegalStateException | PersistenceException ex) {
            // ignore
          }        
        }
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
  public static <T> List<T> matchingEntities(final T the_object, 
                                             final Class<T> the_class) {
    final List<T> result = new ArrayList<T>();
    final Session session = Persistence.currentSession();
    boolean transaction = false;
    
    if (session == null) {
      result.add(the_object);
    } else {
      try {
        // try to use the provided object as an example
        transaction = Persistence.beginTransaction();
        @SuppressWarnings("deprecation") // no query by example in JPA
        final Criteria cr = session.createCriteria(the_class);
        cr.add(Example.create(the_object));
        final List<?> matches = cr.list();
        for (final Object o : matches) {
          if (the_class.isAssignableFrom(o.getClass())) {
            // this is a checked cast even though Java thinks it isn't
            result.add((T) o);
          }
        }
        if (transaction) {
          Persistence.commitTransaction();
        }
      } catch (final HibernateException e) {
        if (transaction) {
          try {
            Persistence.rollbackTransaction();
          } catch (final IllegalStateException | PersistenceException ex) {
            // ignore
          }
          Main.LOGGER.info("exception when searching for object matching " + 
                           the_object + ": " + e);
          result.clear();
          result.add(the_object);
        }
      }
    }
    
    return result;
  }
}
