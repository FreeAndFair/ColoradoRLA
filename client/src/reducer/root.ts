interface AppState {
    greeting: string;
    loggedIn: boolean;
}

const defaultState = {
    greeting: 'Hello',
    loggedIn: false,
};

export default function root(state: AppState = defaultState, action: any) {
    switch (action.type) {

    case 'NEXT_GREETING':
        const greeting
            = state.greeting === 'Hello'
            ? 'Hi'
            : 'Hello';
        return { greeting };

    default:
        return state;
    }
}
