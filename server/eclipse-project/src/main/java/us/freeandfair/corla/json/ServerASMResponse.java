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

import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.UIEvent;

/**
 * The standard response provided by the server to indicate the state of the
 * server's ASM and what UI events are next permitted.
 * @trace endpoints.server_response
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 0.0.1
 */
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
public class ServerASMResponse {
  /**
   * The server's current state.
   */
  @SuppressWarnings("unused")
  private final ASMState my_current_state;
  
  /**
   * The permitted next UI events.
   */
  @SuppressWarnings("unused")
  private final Set<UIEvent> my_enabled_ui_events;
  
  /**
   * Create a new response object.
   * @param the_current_state is the current state of the ASM.
   * @param the_enabled_ui_events are the UI events enabled from the current state.
   */
  public ServerASMResponse(final ASMState the_current_state,
                           final Set<UIEvent> the_enabled_ui_events) {
    my_current_state = the_current_state;
    my_enabled_ui_events = the_enabled_ui_events;
  }
}