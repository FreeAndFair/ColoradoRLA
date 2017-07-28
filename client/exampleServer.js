const connect = require('connect');

const ballotStyles = require('./data/ballotStyles');
const castVoteRecords = require('./data/castVoteRecords');
const contests = require('./data/contests');

const app = connect();

const get = (path, handler) => {
    app.use(path, (req, res, done) => {
        const headers = {
            'access-control-allow-origin': '*',
            'content-type': 'application/json',
        };
        res.writeHead(200, headers);

        const body = JSON.stringify(handler(req, res));
        res.end(body);

        done();
    });
};

get('/ballot-style', (req, res) => ballotStyles);

get('/cvr', (req, res) => castVoteRecords);

get('/contest', (req, res) => contests);


app.listen(4000);
