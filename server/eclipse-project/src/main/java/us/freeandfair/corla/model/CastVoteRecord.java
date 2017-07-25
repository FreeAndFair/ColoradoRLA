/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * 
 * @created Jul 25, 2017
 * 
 * @copyright 2017 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @author Joey Dodds <jdodds@galois.com>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import java.util.List;
import java.util.Set;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class CastVoteRecord {
  /**
   * A list of choices. Presumably this list should correlate to the ordering of
   * some Contest
   */
  private final List<Set<String>> my_cast_vote_record;

  /**
   * The Ballot Manifest info for this Cast Vote Record
   */
  private final BallotManifestInfo my_ballot_manifest_info;
  
  /**
   * 
   * <description>
   * <explanation>
   * @return the cast_vote_record
   */
  public List<Set<String>> getMy_cast_vote_record() {
    assert false;
    //@ assert false;
    return my_cast_vote_record;
  }

  /**
   * @param
   * @return the ballot_manifest_info
   */
  public BallotManifestInfo get_ballot_manifest_info() {
    assert false;
    //@ assert false;
    return my_ballot_manifest_info;
  }

  /**
   * @param
   */
  public CastVoteRecord(List<Set<String>> the_cast_vote_record,
                        BallotManifestInfo the_ballot_manifest_info) {
    super();
    my_cast_vote_record = the_cast_vote_record;
    my_ballot_manifest_info = the_ballot_manifest_info;
  }

  /**
   * @return the cast_vote_record
   */
  public List<Set<String>> get_cast_vote_record() {
    assert false;
    // @ assert false;
    return my_cast_vote_record;
  }
}
