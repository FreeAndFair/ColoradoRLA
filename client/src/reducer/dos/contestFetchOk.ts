import { merge } from 'lodash';

import { parse } from 'corla/adapter/contestFetch';


export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.sos.contests = parse(action.data);

    return nextState;
};
