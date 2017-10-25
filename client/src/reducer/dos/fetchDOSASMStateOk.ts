import * as _ from 'lodash';


function parse(data: any): DOS.ASMState {
    return data.current_state;
}


export default function fetchDOSASMStateOk(
    state: DOS.AppState,
    action: Action.FetchDOSASMStateOk,
): DOS.AppState {
    const nextState = { ...state };

    nextState.asm = parse(action.data);

    return nextState;
}
