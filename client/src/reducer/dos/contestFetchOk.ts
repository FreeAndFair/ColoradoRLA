import { merge } from 'lodash';

import { parse } from 'corla/adapter/contestFetch';


export default (state: any, action: any) => {
    const nextState = { ...state };

    const contests = merge({}, state.contests, parse(action.data));
    nextState.sos = merge({}, state.sos, { contests });

    return nextState;
};
