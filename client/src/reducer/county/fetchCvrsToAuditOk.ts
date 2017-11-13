import * as _ from 'lodash';


export default function fetchCvrsToAuditOk(
    state: County.AppState,
    action: Action.FetchCvrsToAuditOk,
): County.AppState {
    const nextState = { ...state };

    // TODO: Parse data, don't just set the JSON.
    nextState.cvrsToAudit = action.data;

    return nextState;
}
