/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.util.Comparator;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import us.freeandfair.corla.persistence.PersistentEntity;

/**
 * Information about the locations of specific batches of ballots. The
 * Set<BallotManifestInfo> for a given county forms a complete ballot
 * manifest. Joins to a CastVoteRecord to provide a ballot pull list.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Entity
@Immutable // this is a Hibernate-specific annotation, but there is no JPA alternative
@Cacheable(true)
@Table(name = "ballot_manifest_info",
       indexes = { @Index(name = "idx_bmi_county", columnList = "county_id"),
                   @Index(name = "idx_bmi_seqs", columnList = "sequence_start,sequence_end")})
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
final public class BallotManifestInfo implements PersistentEntity, Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1;

  /**
   * The ID number.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long my_id;

  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;

  /**
   * The ID number of the county in which the batch was scanned.
   */
  @Column(name = "county_id", updatable = false, nullable = false)
  private Long my_county_id;
  //@ private invariant my_county_id >= 0;

  /**
   * The ID number of the scanner that scanned the batch.
   */
  @Column(updatable = false, nullable = false)
  private Integer my_scanner_id;

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
   * The first sequence number (of all ballots) in this batch. Used to find a batch
   * based on a random sequence number.
   */
  @Column(updatable = false, nullable = false, name = "sequence_start")
  private Long my_sequence_start;

  /**
   * The last sequence number (of all ballots) in this batch. Used to find a batch
   * based on a random sequence number.
   */
  @Column(updatable = false, nullable = false, name = "sequence_end")
  private Long my_sequence_end;

  /**
   * The unique properties as a string for fast selection
   */
  private String uri;

  /**
   * The projected start of this manifest chunk
   */
  public Long ultimateSequenceStart;

  /**
   * The projected end of this manifest chunk
   */
  public Long ultimateSequenceEnd;

  /**
   * @param start the adjusted sequence start
   * this is like offset and limit of pages of a contest (multiple counties)
   */
  public void setUltimate(final Long start) {
    this.ultimateSequenceStart = start;
    this.ultimateSequenceEnd = start + rangeSize();
  }

  /** from contest scope to county scope **/
  public Integer sequencePosition(final Integer rand) {
    // subtraction gives a 0-based offset, adding one gives us the 1-based
    // ballot position in the file, row number of the file, a.k.a.: cvr.cvrNumber()
    return rand - this.ultimateSequenceStart.intValue() + 1;
  }

  /**
   * translate a generated random number from contest to county scope, then
   * from county to batch scope
   **/
  public Integer translateRand(final Integer rand) {
    return sequencePosition(rand);
  }

  /**
   * @return Long the number of ballots in my chunk of a manifest
   */
  public Long rangeSize() {
    return my_sequence_end - my_sequence_start;
  }

  /** get the uri for fast selection **/
  public String getUri() {
    return this.uri;
  }

  /** set the uri for fast selection **/
  public void setUri() {
    this.uri = String.format("%s:%s:%s-%s",
                             "bmi",
                             countyID(),
                             scannerID(),
                             batchID());
  }

  /**
   * @return Boolean whether this manifest section would hold the random
   * nth selection
   */
  public Boolean isHolding(final Long rand) {
    // setUltimate(last + 1L) means the start and end is inclusive
    return this.ultimateSequenceStart
      <= rand && rand <= this.ultimateSequenceEnd;
  }

  /**
   * Constructs an empty ballot manifest information record, solely
   * for persistence.
   */
  public BallotManifestInfo() {
    super();
  }

  /**
   * Constructs a ballot manifest information record.
   *
   * @param the_county_id The county ID.
   * @param the_scanner_id The scanner ID.
   * @param the_batch_id The batch ID.
   * @param the_batch_size The batch size.
   * @param the_storage_location The storage location.
   */
  public BallotManifestInfo(final Long the_county_id,
                            final Integer the_scanner_id,
                            final String the_batch_id,
                            final int the_batch_size,
                            final String the_storage_location,
                            final Long the_sequence_start,
                            final Long the_sequence_end) {
    super();
    my_county_id = the_county_id;
    my_scanner_id = the_scanner_id;
    my_batch_id = the_batch_id;
    my_batch_size = the_batch_size;
    my_storage_location = the_storage_location;
    my_sequence_start = the_sequence_start;
    my_sequence_end = the_sequence_end;
    this.setUri();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return my_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setID(final Long the_id) {
    my_id = the_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return my_version;
  }

  /**
   * @return the county ID.
   */
  public Long countyID() {
    return my_county_id;
  }

  /**
   * @return the scanner ID.
   */
  public Integer scannerID() {
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
   * @return the sequence start
   */
  public Long sequenceStart() {
    return my_sequence_start;
  }

  /**
   * @return the sequence end
   */
  public Long sequenceEnd() {
    return my_sequence_end;
  }

  /**
   * computed value based on scannerId,batchID, and ballotPosition (in the bin)
   * This is what the auditors will use to confirm they have the correct card.
   **/
  public String imprintedID(final Long rand) {
    return scannerID() + "-" +
           batchID() + "-" +
           ballotPosition(rand.intValue()).toString();
  }

  /**
   * where the ballot sits in it's storage bin
   **/
  public Integer ballotPosition(final Integer sequencePosition) {
    // position is the nth (1 based)
    return sequencePosition - sequenceStart().intValue() + 1;
  }

  /**
   * @return a String representation of this object.
   */
  @Override
  public String toString() {
    return "BallotManifestInfo [" + ", county_id=" + my_county_id +
           ", scanner_id=" + my_scanner_id + ", batch_size=" +
           my_batch_size + ", storage_container=" + my_storage_location + "]";
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
    return nullableHashCode(storageLocation());
  }

  /**
   * sort across counties by comparing countyID() as well as sequenceEnd()
   **/
  public static class Sort implements Comparator<BallotManifestInfo>, Serializable {

    /**
     * a good practice
     */
    public static final long serialVersionUID = 1L;

    /**
     * a good practice
     */
    @Override
    public int compare(final BallotManifestInfo bmi1,
                       final BallotManifestInfo bmi2) {
      if (bmi1.countyID().equals(bmi2.countyID())) {
        return bmi1.sequenceEnd().compareTo(bmi2.sequenceEnd());
      } else {
        return bmi1.countyID().compareTo(bmi2.countyID());
      }
    }
  }
}
