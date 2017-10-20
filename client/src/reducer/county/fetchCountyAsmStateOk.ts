import * as _ from 'lodash';


function parse(data: JSON.FetchCountyASMStateOk): County.ASM {
    return {
        currentState: data.current_state,
        enabledUiEvents: data.enabled_ui_events,
    };
}


export default function fetchCountyAsmStateOk(
    state: County.AppState,
    action: Action.FetchCountyASMStateOk,
): County.AppState {
    const nextState = { ...state };

    nextState.asm!.county = parse(action.data);

    return nextState;
}
