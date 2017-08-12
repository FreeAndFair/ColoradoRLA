import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import authCountyAdmin from './authCountyAdmin';


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


const URL = /\/auth-county-admin$/;


test('authCountyAdmin', s => {
    test('sends the right data', t => {
        const f = setup();
        f.fetch(URL, { body: 'Authenticated', status: 200 });

        t.plan(3);

        const a = authCountyAdmin(f.username, f.password);
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];

        t.equal(args.method, 'post');
        t.deepEqual(args.body, { username: f.username, password: f.password });

        teardown(f);
    });

    test('when authenticated', t => {
        const f = setup();
        f.fetch(URL, { body: 'Authenticated', status: 200 });

        t.plan(1);

        const a = authCountyAdmin(f.username, f.password);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'AUTH_COUNTY_ADMIN_SEND' },
                { type: 'AUTH_COUNTY_ADMIN_RECEIVE' },
            ]);

            teardown(f);
        });
    });

    test('when auth fails', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Authentication failed',
            status: 401,
        });

        t.plan(1);

        const a = authCountyAdmin(f.username, f.password);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'AUTH_COUNTY_ADMIN_SEND' },
                { type: 'AUTH_COUNTY_ADMIN_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = authCountyAdmin(f.username, f.password);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'AUTH_COUNTY_ADMIN_SEND' },
                { type: 'AUTH_COUNTY_ADMIN_NETWORK_FAIL' },
            ]);

            teardown(f);
        });
    });

    s.end();
});
