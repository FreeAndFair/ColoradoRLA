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

// tslint:disable
const exampleState = {
    loggedIn: false,
    county: {
        name: 'County Name',
        info: {
            field1: 'abc',
            field2: 'xyz',
        },
        contests: [
            {
                name: 'Contest A',
                choices: [
                    { id: 1, name: 'Candidate 1' },
                    { id: 2, name: 'Candidate 2' },
                    { id: 3, name: 'Candidate 3' },
                ],
            },
            {
                name: 'Contest B',
                choices: [
                    { id: 4, name: 'Candidate 4' },
                    { id: 5, name: 'Candidate 5' },
                ],
            },
            {
                name: 'Contest C',
                choices: [
                    { id: 7, name: 'Candidate 7' },
                    { id: 8, name: 'Candidate 8' },
                ],
            },
        ],
    },
};
// tslint:enable

export default function root(state: AppState = exampleState, action: any) {
    switch (action.type) {
    default:
        return state;
    }
}
