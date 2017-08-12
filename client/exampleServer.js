const connect = require('connect');

const ballotStyles = require('./data/ballotStyles');
const castVoteRecords = require('./data/castVoteRecords');
const contests = require('./data/contests');


const app = connect();

const route = (m, path, handler) => {
    const headers = contentType => ({
        'access-control-allow-origin': '*',
        'content-type': contentType,
    });

    app.use(path, (req, res, done) => {
        if (req.method.toLowerCase() !== m.toLowerCase()) {
            res.writeHead(405, headers);
            res.end();
            return done();
        }

        const { data, status } = handler(req, res);

        if (typeof data === 'string') {
            res.writeHead(status, headers('text/plain'));
            res.end(data);
        } else {
            res.writeHead(status, headers('application/json'));
            res.end(JSON.stringify(data));
        }

        done();
    });
};

const ok = data => ({ status: 200, data });

const badRequest = msg => ({ status: 400, data: msg });

const unauthorized = msg => ({ status: 401, data: msg });

const notFound = msg => ({ status: 404, data: msg });

const serverError = msg => ({ status: 500, data: msg });


route('get', '/ballot-style', () => ok(ballotStyles));

route('get', '/cvr', () => ok(castVoteRecords));

route('get', '/contest', () => ok(contests));

route('post', '/auth-state-admin', () => ok('Authenticated'));

route('post', '/state-refresh', () => ok({
    auditStage: 'auditOngoing',
    contests: [
        { id: '1001', audit: 'yes', reason: 'small margin' },
        { id: '1002', audit: 'no' },
        { id: '1005', audit: 'yes', reason: 'randomly chosen' },
        { id: '1008', audit: 'handCount' },
        { id: '1005', audit: 'yes', reason: 'important contest' },
    ],
    counties: [
        { id: '12', status: 'noData' },
        { id: '18', status: 'noData' },
        { id: '20', status: 'cvrsUploaded' },
        { id: '25', status: 'noData' },
        { id: '27', status: 'errorInUploadedData' },
        { id: '33', status: 'noData' },
        { id: '39', status: 'cvrsUploaded' },
    ],
    riskLimit: 0.05,
    seed: 'deadbeef',

}));

app.listen(4000);
