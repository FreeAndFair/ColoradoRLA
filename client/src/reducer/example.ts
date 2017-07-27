// tslint:disable
const oneOfManyContest = {
    name: 'One of Many Contest',
    id: 1,
    choices: [
        { id: 1, name: 'Choice 1' },
        { id: 2, name: 'Choice 2' },
        { id: 3, name: 'Choice 3' },
    ],
};

const yesNoContest = {
    name: 'Yes/No Contest',
    id: 2,
    choices: [
        { id: 4, name: 'Yes' },
        { id: 5, name: 'No' },
    ],
};

const someOfManyContest = {
    name: 'Some of Many Contest',
    id: 3,
    choices: [
        { id: 7, name: 'Choice A' },
        { id: 8, name: 'Choice B' },
        { id: 9, name: 'Choice C' },
        { id: 10, name: 'Choice D' },
        { id: 11, name: 'Choice E' },
    ],
};


// `marks` : { [ContestId]: ChoiceId[] }
const b = (id: any, audited: any, style: any, marks: any = {}) =>
    ({ id, audited, style, marks });


const ballots = [
    b(11, false, 'styleA'),
    b(22, false, 'styleB'),
    b(33, true, 'styleC', { 1: [2], 3: [8, 9, 11] }),
    b(44, false, 'sylteC'),
    b(55, true, 'styleA', { 1: [3], 2: [5] }),
];



export const exampleCountyState = {
    name: 'Acme County',

    // County audit status.
    status: 'not-ready',

    // Miscellaneous county-relevant election metadata.
    info: {
        electionDate: '11/21/2017',
        auditDate: '12/1/2017',
    },

    // Contests under audit for this county.
    contests: [1, 2, 3],

    // Ballots selected for audit.
    // When audited, will include observed marks.
    ballots,
};

const ballotStyles = {
    styleA: { name: 'Style A', contests: [1, 2] },
    styleB: { name: 'Style B', contests: [1, 2, 3] },
    styleC: { name: 'Style C', contests: [1, 3] },
};

const contests = {
    1: oneOfManyContest,
    2: yesNoContest,
    3: someOfManyContest,
};

export const exampleState = {
    loggedIn: false,
    county: exampleCountyState,
    ballotStyles,
    contests,
};
