import { exampleState } from './example';


interface CountyAppState {
    name: string;
    info: any;
    contests: any[];
}

interface AppState {
    loggedIn: boolean;
    county?: CountyAppState;
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
