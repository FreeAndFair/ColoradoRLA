const connect = require('connect');

const ballotStyles = require('./data/ballotStyles');
const castVoteRecords = require('./data/castVoteRecords');
const contests = require('./data/contests');


const app = connect();

const route = (m, path, handler) => {
    const headers = {
        'access-control-allow-origin': '*',
        'content-type': 'application/json',
    };

    app.use(path, (req, res, done) => {
        if (req.method.toLowerCase() !== m.toLowerCase()) {
            res.writeHead(405, headers);
            res.end();
            return done();
        }

        const { data, status } = handler(req, res);
        const body = JSON.stringify(data);

        res.writeHead(status, headers);
        res.end(body);

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


app.listen(4000);
