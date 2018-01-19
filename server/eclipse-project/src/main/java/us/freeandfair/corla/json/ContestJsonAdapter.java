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
import java.util.Arrays;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.model.Choice;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.persistence.Persistence;

/**
 * JSON adapter for contest.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity", "PMD.NPathComplexity"})
public final class ContestJsonAdapter extends TypeAdapter<Contest> {
  /**
   * The "id" string (for JSON serialization).
   */
  private static final String ID = "id";
  
  /**
   * The "name" string (for JSON serialization).
   */
  private static final String NAME = "name";
  
  /**
   * The "county_id" string (for JSON serialization).
   */
  private static final String COUNTY_ID = "county_id";
  
  /**
   * The "description" string (for JSON serialization).
   */
  private static final String DESCRIPTION = "description";
  
  /**
   * The "choices" string (for JSON serialization).
   */
  private static final String CHOICES = "choices";
  
  /**
   * The "votes allowed" string (for JSON serialization).
   */
  private static final String VOTES_ALLOWED = "votes_allowed";
  
  /**
   * The "winners allowed" string (for JSON serialization).
   */
  private static final String WINNERS_ALLOWED = "winners_allowed";
  
  /**
   * The "sequence number" string (for JSON serialization).
   */
  private static final String SEQUENCE_NUMBER = "sequence_number";
  
  /**
   * Writes a contest object.
   * 
   * @param the_writer The JSON writer.
   * @param the_info The object to write.
   */ 
  @Override
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public void write(final JsonWriter the_writer, 
                    final Contest the_contest) 
      throws IOException {
    the_writer.beginObject();
    the_writer.name(ID).value(the_contest.id());
    the_writer.name(NAME).value(the_contest.name());
    the_writer.name(COUNTY_ID).value(the_contest.county().id());
    the_writer.name(DESCRIPTION).value(the_contest.description());
    the_writer.name(CHOICES);
    the_writer.beginArray();
    for (final Choice c : the_contest.choices()) {
      if (!c.fictitious()) {
        the_writer.jsonValue(Main.GSON.toJson(Persistence.unproxy(c)));
      }
    }
    the_writer.endArray();
    the_writer.name(VOTES_ALLOWED).value(the_contest.votesAllowed());
    the_writer.name(WINNERS_ALLOWED).value(the_contest.winnersAllowed());
    the_writer.name(SEQUENCE_NUMBER).value(the_contest.sequenceNumber());
    the_writer.endObject();
  }
  
  /**
   * Reads a contest object. 
   * 
   * @param the_reader The JSON reader.
   * @return the object.
   */
  @Override
  public Contest read(final JsonReader the_reader) 
      throws IOException {
    boolean error = false;
    Long contest_id = null;
    String contest_name = null;
    County county = null;
    String description = null;
    Choice[] choices = null;
    Integer votes_allowed = null;
    Integer winners_allowed = null;
    Integer sequence_number = null;
    
    the_reader.beginObject();
    while (the_reader.hasNext()) {
      final String name = the_reader.nextName();
      switch (name) {
        case ID:
          contest_id = the_reader.nextLong();
          break;
        
        case NAME:
          contest_name = the_reader.nextString();
          break;
          
        case COUNTY_ID:
          county = Persistence.getByID(the_reader.nextLong(), County.class);
          break;
          
        case DESCRIPTION:
          description = the_reader.nextString();
          break;
          
        case CHOICES:
          choices = Main.GSON.fromJson(the_reader.nextString(), Choice[].class);
          break;
          
        case VOTES_ALLOWED:
          votes_allowed = the_reader.nextInt();
          break;
          
        case WINNERS_ALLOWED:
          winners_allowed = the_reader.nextInt();
          break;
          
        case SEQUENCE_NUMBER:
          sequence_number = the_reader.nextInt();
          break;
          
        default:
          error = true;
          break;
      }
    }
    the_reader.endObject();
    
    // check if an identically-numbered contest exists
    
    if (error || contest_id == null || county == null || description == null ||
        choices == null || votes_allowed == null || winners_allowed == null) {
      throw new JsonParseException("invalid data in contest");
    }
    
    if (sequence_number == null) {
      sequence_number = 0;
    }
    final Contest existing = Persistence.getByID(contest_id, Contest.class);
    final Contest result;
    if (existing == null) {
      // we don't use the ID because it might conflict with something else
      result = new Contest(contest_name, county, description, 
                           Arrays.asList(choices), votes_allowed,
                           winners_allowed, sequence_number);
    } else {
      result = existing;
    }
    
    return result;
  }
}
