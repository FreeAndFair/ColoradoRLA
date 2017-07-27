// tslint:disable
export const exampleCountyState = {
    name: 'Acme County',
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
};

export const exampleState = {
    loggedIn: false,
    county: exampleCountyState,
};
