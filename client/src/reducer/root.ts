interface AppState {
    loggedIn: boolean;
}

const defaultState = {
    loggedIn: false,
};

export default function root(state: AppState = defaultState, action: any) {
    switch (action.type) {
    default:
        return state;
    }
}
