const ballotStyles = require('./ballotStyles');
const contests = require('./contests');
const counties = require('./counties');
const castVoteRecords = require('./castVoteRecords');


const county = counties[6];

const auditBoard = [
    {
        firstName: 'John',
        lastName: 'Doe',
        partyAffiliation: 'Democratic Party',
    },
    {
        firstName: 'Jane',
        lastName: 'Smith',
        partyAffiliation: 'Republican Party',
    }
];

    // Simulate an autoincrementing database ID using the array index.
const ballots = castVoteRecords.map((cvr, id) => {
    return Object.assign({}, cvr, { id });
});

module.exports = {
    auditBoard,
    ballots,
    currentBallotId: ballots[0].id,
    ballotStyles,
    ballotManifestDigest: '0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a',
    cvrExportDigest: 'fafafafafafafafafafafafafafafafafafafafafafafafafafafafafafafafa',
    contests,
    contestsUnderAudit: contests.map(c => ({
        contest: c,
        reason: 'close_contest',
    })),
    county,
    startTimestamp: (new Date()).toString(),
    ballotCount: castVoteRecords.length,  // 165
    ballotsToAudit: 55,
    ballotsAudited: 10,
    discrepancies: 3,
    disagreements: 1,
};
