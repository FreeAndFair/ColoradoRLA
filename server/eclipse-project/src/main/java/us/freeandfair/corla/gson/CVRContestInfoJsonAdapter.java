/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Jul 28, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.model.CVRContestInfo;
import us.freeandfair.corla.model.CVRContestInfo.ConsensusValue;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;

/**
 * JSON adapter for a map from contests to choices.
 */
// the default constructor suffices for type adapters
@SuppressWarnings("PMD.AtLeastOneConstructor")
/**
 * JSON adapter for the internal map from contests to choices.
 */
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
    the_writer.name(CONSENSUS).value(the_info.consensus().toString());
    the_writer.name(CHOICES);
    the_writer.beginArray();
    for (final Choice c : the_info.choices()) {
      the_writer.value(c.id());
    }
    the_writer.endArray();
    the_writer.endObject();
  }
  
  /**
   * Reads a set of choices.
   */
  private List<Choice> readChoices(final JsonReader the_reader)
      throws IOException {
    final List<Choice> result = new ArrayList<Choice>();
    the_reader.beginArray();
    while (the_reader.hasNext()) {
      final Long choice_id = the_reader.nextLong();
      final Choice c = Choice.byID(choice_id);
      if (c == null) {
        throw new IOException("invalid choice ID (" + choice_id + ") read");
      } else {
        result.add(c);
      }
    }
    the_reader.endArray();
    return result;
  }
  
  /**
   * Reads a CVR contest info objects.
   */
  @Override
  public CVRContestInfo read(final JsonReader the_reader) 
      throws IOException {
    boolean error = false;
    List<Choice> choices = null;
    long contest_id = -1;
    String comment = null;
    ConsensusValue consensus = ConsensusValue.UNDEFINED;
    
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      final String name = the_reader.nextName();
      switch (name) {
        case CONTEST:
          contest_id = the_reader.nextLong();
          break;
        
        case COMMENT:
          comment = the_reader.nextString();
          break;
          
        case CONSENSUS:
          try {
            consensus = ConsensusValue.valueOf(the_reader.nextString());
          } catch (final IllegalArgumentException e) {
            consensus = ConsensusValue.UNDEFINED;
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
    
    if (error) {
      throw new IOException("invalid data detected in CVR contest info");
    }
    
    return CVRContestInfo.instance(Contest.byID(contest_id), comment, 
                                   consensus, choices);
  }
}