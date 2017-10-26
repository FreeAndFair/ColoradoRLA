import { isEmpty, merge } from 'lodash';

import { parse } from 'corla/adapter/countyDashboardRefresh';


function cvrImportAlert(
    prev: County.CVRImportStatus,
    next: County.CVRImportStatus,
): County.CVRImportAlert {
    if (prev.state === 'IN_PROGRESS') {
        if (next.state === 'FAILED') {
            return 'Fail';
        }
        if (next.state === 'SUCCESSFUL') {
            return 'Ok';
        }
    }

    return 'None';
}


export default function dashboardRefreshOk(
    state: County.AppState,
    action: Action.CountyDashboardRefreshOk,
): County.AppState {
    const newState = parse(action.data, state);

    const nextState = merge({}, state, newState);

    // We want to overwrite these, not deeply merge, because an empty
    // value indicates a signed-out audit board or that we are between
    // rounds.
    nextState.auditBoard = newState.auditBoard;
    nextState.currentRound = newState.currentRound;

    nextState.cvrImportAlert = cvrImportAlert(
        state.cvrImportStatus,
        newState.cvrImportStatus,
    );

    return nextState;
}
