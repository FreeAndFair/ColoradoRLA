import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import dosDashboardRefresh from './dosDashboardRefresh';


const setup = () => {
    const data = {
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
    };

    const store = configureStore([thunkMiddleware])({});

    return {
        data,
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
        store,
    };
};

const teardown = (f: any) => {
    fetch.restore();
    f.store.clearActions();
};


const URL = /\/dos-dashboard$/;


test('dosDashboardRefresh', s => {
    test('fetches the data', t => {
        const f = setup();
        f.fetch(URL, { body: f.data, status: 200 });

        t.plan(2);

        const a = dosDashboardRefresh();
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];
        t.equal(args, undefined);

        teardown(f);
    });

    test('when the server sends new state', t => {
        const f = setup();
        f.fetch(URL, { body: f.data, status: 200 });

        t.plan(1);

        const a = dosDashboardRefresh();
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'DOS_DASHBOARD_REFRESH_SEND' },
                { type: 'DOS_DASHBOARD_REFRESH_OK', data: f.data },
            ]);

            teardown(f);
        });
    });

    test('when there is a server error', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Could not refresh State Dashboard data',
            status: 500,
        });

        t.plan(1);

        const a = dosDashboardRefresh();
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'DOS_DASHBOARD_REFRESH_SEND' },
                { type: 'DOS_DASHBOARD_REFRESH_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = dosDashboardRefresh();
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'DOS_DASHBOARD_REFRESH_SEND' },
                { type: 'DOS_DASHBOARD_REFRESH_NETWORK_FAIL' },
            ]);

            teardown(f);
        });
    });

    s.end();
});
