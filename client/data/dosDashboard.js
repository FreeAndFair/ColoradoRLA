const castVoteRecords = require('./castVoteRecords');
const contests = require('./contests');
const counties = require('./counties');


module.exports = {
    auditStage: 'auditOngoing',
    contests,
    counties: counties.map(c => Object.assign({}, c, { status: 'cvrsUploaded' })),
    manifestUploadsComplete: true,
    cvrUploadsComplete: true,
    riskLimit: 0.05,
    seed: 'deadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeefdeadbeef',
    ballots: castVoteRecords,
};
