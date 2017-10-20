import { merge } from 'lodash';

import { parse } from 'corla/adapter/dosDashboardRefresh';


export default function dashboardRefreshOk(
    state: DOS.AppState,
    action: Action.DOSDashboardRefreshOk,
): DOS.AppState {
    const newState = parse(action.data);

    const nextState = merge({}, state, newState);
    nextState.auditedContests = newState.auditedContests;
    nextState.countyStatus = newState.countyStatus;

    return nextState;
}
