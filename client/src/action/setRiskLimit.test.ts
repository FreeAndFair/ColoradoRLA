import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import setRiskLimit from './setRiskLimit';


const setup = () => {
    const riskLimit = 0.05;

    const store = configureStore([thunkMiddleware])({});

    return {
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
        riskLimit,
        store,
    };
};

const teardown = (f: any) => {
    fetch.restore();
    f.store.clearActions();
};


const URL = /\/risk-limit-comp-audits$/;


test('setRiskLimit', s => {
    test('sends the right data', t => {
        const f = setup();
        f.fetch(URL, { body: 'Risk limit set', status: 200 });

        t.plan(3);

        const a = setRiskLimit(f.riskLimit);
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];

        t.equal(args.method, 'post');
        t.deepEqual(args.body, { riskLimit: f.riskLimit });

        teardown(f);
    });

    test('when the risk limit is set', t => {
        const f = setup();
        f.fetch(URL, { body: 'Risk limit set', status: 200 });

        t.plan(1);

        const a = setRiskLimit(f.riskLimit);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SET_RISK_LIMIT_SEND' },
                { type: 'SET_RISK_LIMIT_RECEIVE' },
            ]);

            teardown(f);
        });
    });

    test('when the risk limit is invalid', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Invalid risk limit specified',
            status: 400,
        });

        t.plan(1);

        const a = setRiskLimit(f.riskLimit);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SET_RISK_LIMIT_SEND' },
                { type: 'SET_RISK_LIMIT_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when there is a server error', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Could not set a risk limit',
            status: 500,
        });

        t.plan(1);

        const a = setRiskLimit(f.riskLimit);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SET_RISK_LIMIT_SEND' },
                { type: 'SET_RISK_LIMIT_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = setRiskLimit(f.riskLimit);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SET_RISK_LIMIT_SEND' },
                { type: 'SET_RISK_LIMIT_NETWORK_FAIL' },
            ]);

            teardown(f);
        });
    });

    s.end();
});
