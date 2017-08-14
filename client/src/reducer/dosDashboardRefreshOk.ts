import { parse } from '../adapter/dosDashboardRefresh';


export default (state: any, action: any) => {
    const nextState = { ...state };

    nextState.sos = {
        ...state.sos,
        ...parse(action.data),
    };

    return nextState;
};
