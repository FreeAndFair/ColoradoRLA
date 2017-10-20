import * as _ from 'lodash';


function parse(data: JSON.FetchDosASMStateOk): DOS.ASM {
    return {
        currentState: data.current_state,
        enabledUiEvents: data.enabled_ui_events,
    };
}


export default function fetchDOSASMStateOk(
    state: DOS.AppState,
    action: Action.FetchDOSASMStateOk,
): DOS.AppState {
    const nextState = { ...state };

    nextState.asm = parse(action.data);

    return nextState;
}
