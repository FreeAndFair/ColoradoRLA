import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import createFetchAction from './createFetchAction';


const setup = () => {
    const data = { the: 'example data' };

    const store = configureStore([thunkMiddleware])({});

    return {
        data,
        failType: 'EXAMPLE_FAIL',
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
        init: {
            credentials: 'include',
            method: 'get',
        },
        networkFailType: 'EXAMPLE_NETWORK_FAIL',
        okType: 'EXAMPLE_OK',
        sendType: 'EXAMPLE_SEND',
        store,
    };
};

const teardown = (f: any) => {
    fetch.restore();
    f.store.clearActions();
};


const URL = 'http://localhost:4000/example';


test('createFetchAction', s => {
    test('fetches the data', t => {
        const f = setup();
        f.fetch(URL, { body: f.data, status: 200 });

        t.plan(2);

        const a = createFetchAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })();
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];
        t.deepEqual(args, f.init);

        teardown(f);
    });

    test('when the response is ok', t => {
        const f = setup();
        f.fetch(URL, { body: f.data, status: 200 });

        t.plan(1);

        const a = createFetchAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })();
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: f.sendType },
                { type: f.okType, data: f.data },
            ]);

            teardown(f);
        });
    });

    test('when there is a server error', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Server error',
            status: 500,
        });

        t.plan(1);

        const a = createFetchAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })();
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: f.sendType },
                { type: f.failType },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = createFetchAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })();
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: f.sendType },
                { type: f.networkFailType },
            ]);

            teardown(f);
        });
    });

    s.end();
});
