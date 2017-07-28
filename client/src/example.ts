// tslint:disable
const oneOfManyContest = {
    name: 'One of Many Contest',
    description: 'A contest with 3 choices, where 1 may be selected.',
    id: '1',
    choices: [
        { id: '1', name: 'Choice 1' },
        { id: '2', name: 'Choice 2' },
        { id: '3', name: 'Choice 3' },
    ],
    votesAllowed: 1,
};

const yesNoContest = {
    name: 'Yes/No Contest',
    description: 'A contest with 2 choices, where 1 may be selected.',
    id: '2',
    choices: [
        { id: '4', name: 'Yes' },
        { id: '5', name: 'No' },
    ],
    votesAllowed: 1,
};

const someOfManyContest = {
    name: 'Some of Many Contest',
    description: 'A contest with 5 choices, where 3 may be selected.',
    id: '3',
    choices: [
        { id: '7', name: 'Choice A' },
        { id: '8', name: 'Choice B' },
        { id: '9', name: 'Choice C' },
        { id: '10', name: 'Choice D' },
        { id: '11', name: 'Choice E' },
    ],
    votesAllowed: 3,
};

const ballotStyles = {
    styleA: {
        name: 'Style A',
        contests: [oneOfManyContest, yesNoContest],
    },
    styleB: {
        name: 'Style B',
        contests: [oneOfManyContest, yesNoContest, someOfManyContest],
    },
    styleC: {
        name: 'Style C',
        contests: [oneOfManyContest, someOfManyContest],
    },
};

// `marks` : { [ContestId]: ChoiceId[] }
const b = (id: any, audited: any, style: any, marks: any = {}) =>
    ({ id, audited, style, marks });

const ballots = [
    b(11, false, ballotStyles.styleA),
    b(22, false, ballotStyles.styleB),
    b(33, true, ballotStyles.styleC, { 1: ['2'], 3: ['8', '9', '11'] }),
    b(44, false, ballotStyles.styleC),
    b(55, true, ballotStyles.styleA, { 1: ['3'], 2: ['5'] }),
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
    contests: [
        oneOfManyContest,
        yesNoContest,
        someOfManyContest,
    ],

    // Ballots selected for audit.
    // When audited, will include observed marks.
    ballots,

    currentBallotId: 22,
};

const contests = {
    '1': oneOfManyContest,
    '2': yesNoContest,
    '3': someOfManyContest,
};

export const exampleState = {
    loggedIn: false,
    county: exampleCountyState,
    ballotStyles,
    contests,
};


export default exampleState;
