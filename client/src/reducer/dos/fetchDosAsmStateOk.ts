import * as _ from 'lodash';


function parse(data: JSON.FetchDosASMStateOk): DOS.ASM {
    return {
        currentState: data.current_state,
        enabledUiEvents: data.enabled_ui_events,
    };
}


export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    if (!nextState.sos) { return nextState; }

    nextState.sos.asm = parse(action.data);

    return nextState;
};
