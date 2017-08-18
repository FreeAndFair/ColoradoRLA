import { merge } from 'lodash';

import { parse } from '../adapter/countyDashboardRefresh';


export default (state: any, action: any) => {
    const nextState = merge({}, state );

    nextState.county = merge({}, state.county, parse(action.data, state));

    return nextState;
};
