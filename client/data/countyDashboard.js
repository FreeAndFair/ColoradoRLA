const contests = require('./contests');
const castVoteRecords = require('./castVoteRecords');


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


module.exports = {
    auditBoard,
    ballotManifestDigest: '0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a0a',
    cvrExportDigest: 'fafafafafafafafafafafafafafafafafafafafafafafafafafafafafafafafa',
    contests,
    contestsUnderAudit: contests.map(c => ({
        contest: c,
        reason: 'close_contest',
    })),
    startTimestamp: (new Date()).toString(),
    ballotCount: castVoteRecords.length,  // 165
    ballotsToAudit: 55,
    ballotsAudited: 10,
    discrepancies: 3,
    disagreements: 1,
};
