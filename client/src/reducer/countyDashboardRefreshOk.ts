import { isEmpty, merge } from 'lodash';

import { parse } from '../adapter/countyDashboardRefresh';


export default (state: any, action: any) => {
    const nextState = merge({}, state );

    const newCountyData = parse(action.data, state);

    nextState.county = merge({}, state.county, newCountyData);

    // We want to overwrite this, not deeply merge, because an empty
    // array might indicate a signed-out audit board.
    nextState.county.auditBoard = newCountyData.auditBoard;

    return nextState;
};
