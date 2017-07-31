import * as _ from 'lodash';


// tslint:disable
const oneOfManyContest = {
    name: 'Regent of the University of Colorado - At Large',
    description: 'A contest with 3 choices, where 1 may be selected.',
    id: '1',
    choices: [
        { id: '1', name: 'Harley Acosta' },
        { id: '2', name: 'Tessa Mckinney' },
        { id: '3', name: 'Regina Robles' },
    ],
    votesAllowed: 1,
};

const yesNoContest = {
    name: 'Proposition 107 (Statutory)',
    description: '',
    id: '2',
    choices: [
        { id: '4', name: 'Yes' },
        { id: '5', name: 'No' },
    ],
    votesAllowed: 1,
};

const someOfManyContest = {
    name: 'COUNTY COMMISSIONER - At Large',
    description: 'A contest with 5 choices, where 3 may be selected.',
    id: '3',
    choices: [
        { id: '7', name: 'Maya Mclaughlin' },
        { id: '8', name: 'Rachelle Kramer' },
        { id: '9', name: 'Nina Jimenez' },
        { id: '10', name: 'Felicia Reid' },
        { id: '11', name: 'Julian Jenkins' },
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

const m = (style: any) => {
    const marks: any = {};

    for (const c of style.contests) {
        marks[c.id] = { choices: [], comments: '' };
    }

    return marks;
};

// `marks` : { [ContestId]: ChoiceId[] }
const b = (id: any, audited: any, style: any) =>
    ({ id, audited, style, marks: m(style) });

const ballots = [
    b(11, false, ballotStyles.styleA),
    b(22, false, ballotStyles.styleB),
    b(33, false, ballotStyles.styleC),
    b(44, false, ballotStyles.styleC),
    b(55, true, ballotStyles.styleA),
    b(66, false, ballotStyles.styleB),
    b(77, false, ballotStyles.styleC),
];

export const exampleCountyState = {
    name: 'Acme County',

    // County audit status.
    status: 'not-ready',

    // Miscellaneous county-relevant election metadata.
    info: {
        electionDate: '11/07/2017',
        auditDate: '11/18/2017',
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

    auditBoard: [
        {
            name: 'John Smith',
            party: 'Democratic Party'
        },
        {
            name: 'Jane Doe',
            party: 'Republican Party'
        },
    ],
};

const contests = {
    '1': oneOfManyContest,
    '2': yesNoContest,
    '3': someOfManyContest,
};

const c = (id: any, name: any, started: any, submitted: any, discrepancies: any) =>
    ({ id, name, started, submitted, discrepancies });

const exampleCounties: any = {
    1001: c(1001, 'County 1001', '11/15/2017 15:00 MST', 207, 6),
    1002: c(1002, 'County 1002', false, 359, 2),
    1003: c(1003, 'County 1003', false, 999, 25),
};

const exampleSoSState: any = {
    counties: exampleCounties,
};

export const exampleState = {
    loggedIn: false,
    county: exampleCountyState,
    ballotStyles,
    contests,
    sos: exampleSoSState,
};


export default exampleState;
