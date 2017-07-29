import * as _ from 'lodash';


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

export const exampleState = {
    loggedIn: false,
    county: exampleCountyState,
    ballotStyles,
    contests,
};


export default exampleState;
