import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import authStateAdmin from './authStateAdmin';


const setup = () => {
    const username = 'user@example.com';
    const password = 'hunter2';

    const store = configureStore([thunkMiddleware])({});

    return {
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
        password,
        store,
        username,
    };
};

const teardown = (f: any) => {
    fetch.restore();
    f.store.clearActions();
};


const URL = /\/auth-state-admin$/;


test('authStateAdmin', s => {
    test('sends the right data', t => {
        const f = setup();
        f.fetch(URL, { body: {}, status: 200 });

        t.plan(3);

        const a = authStateAdmin(f.username, f.password);
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];

        t.equal(args.method, 'post');
        t.deepEqual(args.body, { username: f.username, password: f.password });

        teardown(f);
    });

    test('when authenticated', t => {
        const f = setup();
        f.fetch(URL, { body: {}, status: 200 });

        t.plan(1);

        const a = authStateAdmin(f.username, f.password);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'AUTH_STATE_ADMIN_SEND' },
                { type: 'AUTH_STATE_ADMIN_OK', data: {} },
            ]);

            teardown(f);
        });
    });

    test('when auth fails', t => {
        const f = setup();
        f.fetch(URL, {
            body: { error: 'Authentication failed' },
            status: 401,
        });

        t.plan(1);

        const a = authStateAdmin(f.username, f.password);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'AUTH_STATE_ADMIN_SEND' },
                { type: 'AUTH_STATE_ADMIN_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = authStateAdmin(f.username, f.password);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'AUTH_STATE_ADMIN_SEND' },
                { type: 'AUTH_STATE_ADMIN_NETWORK_FAIL' },
            ]);

            teardown(f);
        });
    });

    s.end();
});
