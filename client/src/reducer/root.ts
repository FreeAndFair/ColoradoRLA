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
    default:
        return state;
    }
}
