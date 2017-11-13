import { merge } from 'lodash';

import { parse } from 'corla/adapter/contestFetch';


export default function contestFetchOk(
    state: DOS.AppState,
    action: Action.DOSFetchContestsOk,
): DOS.AppState {
    const nextState = { ...state };

    nextState.contests = parse(action.data);

    return nextState;
}
