import * as _ from 'lodash';


function parse(data: any): County.ASMState {
    return data.current_state;
}


export default function fetchCountyASMStateOk(
    state: County.AppState,
    action: Action.FetchCountyASMStateOk,
): County.AppState {
    const nextState = { ...state };

    nextState.asm.county = parse(action.data);

    return nextState;
}
