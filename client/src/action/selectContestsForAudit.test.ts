import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import selectContestsForAudit from './selectContestsForAudit';


const setup = () => {
    const contests = ['1001', '1002', '1003'];

    const store = configureStore([thunkMiddleware])({});

    return {
        contests,
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
        store,
    };
};

const teardown = (f: any) => {
    fetch.restore();
    f.store.clearActions();
};


const URL = /\/select-contests$/;


test('selectContestsForAudit', s => {
    test('sends the right data', t => {
        const f = setup();
        f.fetch(URL, { body: 'Contests selected', status: 200 });

        t.plan(3);

        const a = selectContestsForAudit(f.contests);
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];

        t.equal(args.method, 'post');
        t.deepEqual(args.body, f.contests);

        teardown(f);
    });

    test('when the contests are selected', t => {
        const f = setup();
        f.fetch(URL, { body: 'Contests selected', status: 200 });

        t.plan(1);

        const a = selectContestsForAudit(f.contests);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SELECT_CONTESTS_FOR_AUDIT_SEND' },
                { type: 'SELECT_CONTESTS_FOR_AUDIT_OK' },
            ]);

            teardown(f);
        });
    });

    test('when the contests are malformed', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Invalid contest selection data',
            status: 422,
        });

        t.plan(1);

        const a = selectContestsForAudit(f.contests);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SELECT_CONTESTS_FOR_AUDIT_SEND' },
                { type: 'SELECT_CONTESTS_FOR_AUDIT_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when there is a server error', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Could not select contests',
            status: 500,
        });

        t.plan(1);

        const a = selectContestsForAudit(f.contests);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SELECT_CONTESTS_FOR_AUDIT_SEND' },
                { type: 'SELECT_CONTESTS_FOR_AUDIT_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = selectContestsForAudit(f.contests);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'SELECT_CONTESTS_FOR_AUDIT_SEND' },
                { type: 'SELECT_CONTESTS_FOR_AUDIT_NETWORK_FAIL' },
            ]);

            teardown(f);
        });
    });

    s.end();
});
