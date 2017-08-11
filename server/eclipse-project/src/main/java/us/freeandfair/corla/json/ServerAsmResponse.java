/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Free & Fair
 * @license GNU General Public License 3.0
 * @author Joe Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.util.Set;

import us.freeandfair.corla.asm.AsmState;
import us.freeandfair.corla.asm.UiToAsmEventRelation.UiEvent;

/**
 * The standard response provided by the server to indicate the state of the
 * server's ASM and what UI events are next permitted.
 * @trace endpoints.server_response
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField", "URF_UNREAD_FIELD"})
public class ServerAsmResponse {
  /**
   * The server's current state.
   */
  private final AsmState my_current_state;
  
  /**
   * The enabled UI events.
   */
  private final Set<UiEvent> my_enabled_ui_events;
  
  /**
   * Create a new response object.
   * @param the_current_state is the current state of the ASM.
   * @param the_enabled_ui_events are the UI events enabled from the current state.
   */
  public ServerAsmResponse(final AsmState the_current_state,
                           final Set<UiEvent> the_enabled_ui_events) {
    my_current_state = the_current_state;
    my_enabled_ui_events = the_enabled_ui_events;
  }
  
  /**
   * @return the current state.
   */
  public AsmState currentState() {
    return my_current_state;
  }
  
  /**
   * @return the enabled UI events. 
   */
  public Set<UiEvent> enabledUiEvents() {
    return my_enabled_ui_events;
  }
}
