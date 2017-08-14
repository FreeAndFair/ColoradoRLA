import { parse } from '../adapter/contestFetch';


export default (state: any, action: any) => {
    const nextState = { ...state };

    const contests = { ...state.contests, ...parse(action.data) };
    nextState.county = { ...state.county, contests };

    return nextState;
};
