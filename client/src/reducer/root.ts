import { exampleState } from './example';


interface AppState {
    loggedIn: boolean;
    county?: any;
}

const defaultState = {
    loggedIn: false,
};


export default function root(state: AppState = exampleState, action: any) {
    switch (action.type) {
    default:
        return state;
    }
}
