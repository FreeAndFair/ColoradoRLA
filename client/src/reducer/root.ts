interface AppState {
    loggedIn: boolean;
    county?: any;
}

const defaultState = {
    loggedIn: false,
};


export default function root(state: AppState = defaultState, action: any) {
    switch (action.type) {
    case 'LOGIN':
        return { ...state, loggedIn: true };
    case 'FETCH_INITIAL_STATE_SEND':
        // TODO: add flag to indicate pending fetch.
        return state;
    case 'FETCH_INITIAL_STATE_RECEIVE':
        // TODO: should be a deep merge.
        return action.data;
    default:
        return state;
    }
}
