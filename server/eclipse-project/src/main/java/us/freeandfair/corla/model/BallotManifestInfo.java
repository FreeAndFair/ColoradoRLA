/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import us.freeandfair.corla.hibernate.Persistence;

/**
 * Information about the locations of specific batches of ballots.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 0.0.1
 */
@Entity
@Table(name = "ballot_manifest_info")
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class BallotManifestInfo {
  /**
   * The current ID number to be used.
   */
  private static long current_id;

  /**
   * The table of objects that have been created.
   */
  private static final Map<BallotManifestInfo, BallotManifestInfo> CACHE = 
      new HashMap<BallotManifestInfo, BallotManifestInfo>();
  
  /**
   * The table of objects by ID.
   */
  private static final Map<Long, BallotManifestInfo> BY_ID =
      new HashMap<Long, BallotManifestInfo>();
  
  /**
   * The database ID for this ballot manifest info.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false, nullable = false)
  private Long my_id = getID();
  
  /**
   * The timestamp for this ballot manifest info, in milliseconds since the epoch.
   */
  @Column(updatable = false, nullable = false)
  private Instant my_timestamp;
  
  /**
   * The ID number of the county in which the batch was scanned.
   */
  @Column(updatable = false, nullable = false)
  private String my_county_id;
  //@ private invariant my_county_id >= 0;
  
  /**
   * The ID number of the scanner that scanned the batch.
   */
  @Column(updatable = false, nullable = false)
  private String my_scanner_id;

  /**
   * The batch number.
   */
  @Column(updatable = false, nullable = false)
  private String my_batch_id;
  
  /**
   * The size of the batch.
   */
  @Column(updatable = false, nullable = false)
  private Integer my_batch_size;
  
  /**
   * The storage location for the batch.
   */
  @Column(updatable = false, nullable = false)
  private String my_storage_location;
 
  /** 
   * Constructs an empty ballot manifest information record, solely for persistence.
   */
  protected BallotManifestInfo() {
    // default values for everything
  }
  
  /**
   * Constructs a ballot manifest information record.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county_id The county ID.
   * @param the_scanner_id The scanner ID.
   * @param the_batch_id The batch ID.
   * @param the_batch_size The batch size.
   * @param the_storage_location The storage location.
   */
  protected BallotManifestInfo(final Instant the_timestamp,
                               final String the_county_id, final String the_scanner_id, 
                               final String the_batch_id, final int the_batch_size, 
                               final String the_storage_location) {
    my_timestamp = the_timestamp;
    my_county_id = the_county_id;
    my_scanner_id = the_scanner_id;
    my_batch_id = the_batch_id;
    my_batch_size = the_batch_size;
    my_storage_location = the_storage_location;
  }
  
  /**
   * @return the next ID
   */
  private static synchronized long getID() {
    return current_id++;
  }
  
  /**
   * Returns a ballot manifest info with the specified parameters.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county_id The county ID.
   * @param the_scanner_id The scanner ID.
   * @param the_batch_id The batch ID.
   * @param the_batch_size The batch size.
   * @param the_storage_location The storage location.
   */
  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  public static synchronized BallotManifestInfo instance(final Instant the_timestamp,
                                                         final String the_county_id, 
                                                         final String the_scanner_id, 
                                                         final String the_batch_id, 
                                                         final int the_batch_size, 
                                                         final String the_storage_location) {
    BallotManifestInfo result = 
        Persistence.matchingEntity(new BallotManifestInfo(the_timestamp, the_county_id,
                                                     the_scanner_id, the_batch_id,
                                                     the_batch_size, the_storage_location),
                              BallotManifestInfo.class);
    if (!Persistence.isEnabled()) {
      // assign an ID ourselves because persistence is not enabled
      result.my_id = getID();
    }
    // eventually: disable caching entirely in the presence of persistence
    if (CACHE.containsKey(result)) {
      result = CACHE.get(result);
    } else {
      CACHE.put(result, result);
      BY_ID.put(result.id(), result);
    }
    return result;
  }
  
  /**
   * Returns the ballot manifest info with the specified ID.
   * 
   * @param the_id The ID.
   * @return the ballot manifest info, or null if it doesn't exist.
   */
  public static synchronized BallotManifestInfo byID(final long the_id) {
    return BY_ID.get(the_id);
  }
  
  /**
   * "Forgets" the specified ballot manifest info.
   * 
   * @param the_bmi The info to "forget".
   */
  public static synchronized void forget(final BallotManifestInfo the_bmi) {
    CACHE.remove(the_bmi);
    BY_ID.remove(the_bmi.id());
  }
  
  /**
   * Gets all known ballot manifest information for the specified counties.
   * 
   * @param the_county_ids The county ID to retrieve ballot manifest information 
   * for; if this set is empty or null, all ballot manifest information for all 
   * counties is retrieved.
   * @return the requested ballot manifest information.
   */
  public static synchronized Collection<BallotManifestInfo> 
      getMatching(final Set<String> the_county_ids) {
    final Set<BallotManifestInfo> result = new HashSet<BallotManifestInfo>();
    final Set<String> counties_to_check = new HashSet<String>(); 
    
    if (the_county_ids != null) {
      counties_to_check.addAll(the_county_ids);
    }
    
    if (Persistence.isEnabled()) {
      final BallotManifestInfo template = new BallotManifestInfo();
      if (counties_to_check.isEmpty()) {
        result.addAll(Persistence.matchingEntities(template, BallotManifestInfo.class));
      } else {
        for (final String county_id : counties_to_check) {
          template.my_county_id = county_id;
          result.addAll(Persistence.matchingEntities(template, 
                                                     BallotManifestInfo.class));
        }
      } 
    } else {
      for (final BallotManifestInfo bmi : CACHE.keySet()) {
        if (counties_to_check.contains(bmi.countyID())) {
          result.add(bmi);
        }
      }
    }
    
    return result;
  }

  /**
   * @return all known ballot manifest information.
   */
  public static synchronized Collection<BallotManifestInfo> getAll() {
    final Set<BallotManifestInfo> result = new HashSet<BallotManifestInfo>();
    
    if (Persistence.isEnabled()) {
      result.addAll(Persistence.matchingEntities(new BallotManifestInfo(), 
                                                 BallotManifestInfo.class));
    } else {
      result.addAll(CACHE.keySet());
    }
    
    return result;
  }

  /**
   * @return the database ID.
   */
  public long id() {
    return my_id;
  }
  
  /**
   * @return the timestamp.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the county ID.
   */
  public String countyID() {
    return my_county_id;
  }  

  /**
   * @return the scanner ID.
   */
  public String scannerID() {
    return my_scanner_id;
  }
  
  /**
   * @return the batch number.
   */
  public String batchID() {
    return my_batch_id;
  }
  
  /**
   * @return the batch size.
   */
  public Integer batchSize() {
    return my_batch_size;
  }
  
  /**
   * @return the storage container number.
   */
  public String storageLocation() {
    return my_storage_location;
  }  
  
  /**
   * @return a String representation of this object.
   */
  @Override
  public String toString() {
    return "BallotManifestInfo [timestamp=" + my_timestamp + 
        ", county_id=" + my_county_id + ", scanner_id=" + my_scanner_id + 
        ", batch_size=" + my_batch_size +
        ", storage_container=" + my_storage_location + "]";
  }
  
  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = true;
    if (the_other instanceof BallotManifestInfo) {
      final BallotManifestInfo other_bmi = (BallotManifestInfo) the_other;
      result &= nullableEquals(other_bmi.timestamp(), timestamp());
      result &= nullableEquals(other_bmi.countyID(), countyID());
      result &= nullableEquals(other_bmi.scannerID(), scannerID());
      result &= nullableEquals(other_bmi.batchID(), batchID());
      result &= nullableEquals(other_bmi.batchSize(), batchSize());
      result &= nullableEquals(other_bmi.storageLocation(), storageLocation());
    } else {
      result = false;
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  @Override
  public int hashCode() {
    return toString().hashCode();
  }
}
