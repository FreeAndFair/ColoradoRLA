/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 10, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

import java.util.Set;

import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.UIEvent;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * The standard response provided by the server to indicate the state of the
 * server's ASM and what UI events are next permitted.
 * @trace endpoints.server_response
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"unused", "PMD.UnusedPrivateField", "PMD.SingularField"})
@SuppressFBWarnings(value = {"URF_UNREAD_FIELD"}, justification = "Field is read by Gson.")
public class ServerASMResponse {
  /**
   * The server's current state.
   */
  private final ASMState my_current_state;
  
  /**
   * The permitted next UI events.
   */
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
