import * as _ from 'lodash';


function parse(data: any) {
    return {
        currentState: data.current_state,
        enabledUiEvents: data.enabled_ui_events,
    };
}


export default function fetchAuditBoardAsmStateOk(
    state: County.AppState,
    action: Action.FetchAuditBoardASMStateOk,
): County.AppState {
    const nextState = { ...state };

    nextState.asm!.auditBoard = parse(action.data);

    return nextState;
}
