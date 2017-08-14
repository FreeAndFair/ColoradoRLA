import { parse } from '../adapter/countyDashboardRefresh';


export default (state: any, action: any) => {
    const nextState = { ...state };

    const newState = parse(action.data, state);
    nextState.county = { ...state.county, ...newState };

    return nextState;
};
