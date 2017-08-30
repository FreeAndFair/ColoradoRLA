import { isEmpty, merge } from 'lodash';

import { parse } from '../adapter/countyDashboardRefresh';


export default (state: any, action: any) => {
    const nextState = merge({}, state );

    const newCountyData = parse(action.data, state);

    nextState.county = merge({}, state.county, newCountyData);

    // We want to overwrite these, not deeply merge, because an empty
    // value indicates a signed-out audit board or that we are between
    // rounds.
    nextState.county.auditBoard = newCountyData.auditBoard;
    nextState.county.currentRound = newCountyData.currentRound;

    return nextState;
};
