/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 28, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.persistence.Persistence;

/**
 * JSON adapter for CVR contest information.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class CVRContestInfoJsonAdapter 
    extends TypeAdapter<CVRContestInfo> {
  /**
   * The "contest" string (for JSON serialization).
   */
  private static final String CONTEST = "contest";
  
  /**
   * The "choices" string (for JSON serialization).
   */
  private static final String CHOICES = "choices";
  
  /**
   * The "comment" string (for JSON serialization).
   */
  private static final String COMMENT = "comment";
  
  /**
   * THe "comments" string (for erroneous client JSON).
   */
  private static final String COMMENTS = "comments";
  
  /**
   * The "consensus" string (for JSON serialization).
   */
  private static final String CONSENSUS = "consensus";

  /**
   * Writes a CVR contest info object.
   * 
   * @param the_writer The JSON writer.
   * @param the_info The object to write.
   */ 
  @Override
  public void write(final JsonWriter the_writer, 
                    final CVRContestInfo the_info) 
      throws IOException {
    the_writer.beginObject();
    the_writer.name(CONTEST).value(the_info.contest().id());
    the_writer.name(COMMENT).value(the_info.comment());
    if (the_info.consensus() != null) {
      the_writer.name(CONSENSUS).value(the_info.consensus().toString());
    }
    the_writer.name(CHOICES);
    the_writer.beginArray();
    for (final String c : the_info.choices()) {
      the_writer.value(c);
    }
    the_writer.endArray();
    the_writer.endObject();
  }
  
  /**
   * Reads a set of choices.
   */
  private List<String> readChoices(final JsonReader the_reader)
      throws IOException {
    final List<String> result = new ArrayList<String>();
    the_reader.beginArray();
    while (the_reader.hasNext()) {
      result.add(the_reader.nextString());
    }
    the_reader.endArray();
    return result;
  }
  
  /**
   * Checks the sanity of a contest against a set of choices.
   * 
   * @param the_id The contest ID.
   * @param the_choices The choices.
   * @return the resulting contest, if the data is sane, or null if the
   * data is invalid.
   */
  private Contest contestSanityCheck(final Long the_id, 
                                     final List<String> the_choices) {
    final Contest result = Persistence.getByID(the_id, Contest.class);
    boolean error = the_choices == null;
    
    if (!error && result != null) {
      for (final String c : the_choices) {
        if (!result.isValidChoice(c)) {
          error = true;
        }
      }
    }
    
    if (error) {
      return null;
    } else {
      return result;
    }
  }
  
  /**
   * Reads a CVR contest info object.
   * 
   * @param the_reader The JSON reader.
   * @return the object.
   */
  @Override
  public CVRContestInfo read(final JsonReader the_reader) 
      throws IOException {
    boolean error = false;
    List<String> choices = null;
    long contest_id = -1;
    String comment = null;
    ConsensusValue consensus = null;
    
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      final String name = the_reader.nextName();
      switch (name) {
        case CONTEST:
          contest_id = the_reader.nextLong();
          break;
        
        case COMMENT:
        case COMMENTS:
          comment = the_reader.nextString();
          break;
          
        case CONSENSUS:
          try {
            consensus = ConsensusValue.valueOf(the_reader.nextString());
          } catch (final IllegalArgumentException e) {
            // assume undefined consensus, because enum value was invalid
          }
          break;
          
        case CHOICES:
          choices = readChoices(the_reader);
          break;
          
        default:
          error = true;
          break;
      }
    }
    the_reader.endObject();
    
    // check the sanity of the contest
    
    final Contest contest = contestSanityCheck(contest_id, choices);
    
    if (error || contest == null) {
      throw new JsonSyntaxException("invalid data detected in CVR contest info");
    }
    
    return new CVRContestInfo(contest, comment, consensus, choices);
  }
}
