import * as _ from 'lodash';


function parse(data: FetchCountyAsmStateOkJson): CountyAsm {
    return {
        currentState: data.current_state,
        enabledUiEvents: data.enabled_ui_events,
    };
}


export default (state: AppState, action: any): AppState => {
    const nextState = { ...state };

    nextState.county.asm.county = parse(action.data);

    return nextState;
};
