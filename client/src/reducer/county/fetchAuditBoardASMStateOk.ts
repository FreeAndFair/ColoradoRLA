import * as _ from 'lodash';


function parse(data: any): AuditBoardASMState {
    return data.current_state;
}


export default function fetchAuditBoardASMStateOk(
    state: County.AppState,
    action: Action.FetchAuditBoardASMStateOk,
): County.AppState {
    const nextState = { ...state };

    nextState.asm.auditBoard = parse(action.data);

    return nextState;
}
