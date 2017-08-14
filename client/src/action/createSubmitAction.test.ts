import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import createSubmitAction from './createSubmitAction';


const setup = () => {
    const data = { the: 'reply' };

    const store = configureStore([thunkMiddleware])({});

    return {
        body: { the: 'submitted data' },
        data,
        failType: 'EXAMPLE_FAIL',
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
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


test('createSubmitAction', s => {
    test('submits the data', t => {
        const f = setup();
        f.fetch(URL, { body: f.data, status: 200 });

        t.plan(3);

        const a = createSubmitAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })(f.body);
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'submitted to the URL');

        const args: any = fetch.lastCall()[1];

        t.equal(args.method, 'post', 'with the right HTTP method');
        t.deepEqual(args.body, f.body, 'with the right HTTP body');

        teardown(f);
    });

    test('when the response is ok', t => {
        const f = setup();
        const body = { the: 'body' };
        f.fetch(URL, { body, status: 200 });

        t.plan(1);

        const sent = { the: 'sent data' };
        const a = createSubmitAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })(sent);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: f.sendType },
                { type: f.okType, data: body, sent },
            ]);

            teardown(f);
        });
    });

    test('when the request is bad', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Bad request',
            status: 400,
        });

        t.plan(1);

        const a = createSubmitAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })(f.body);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: f.sendType },
                { type: f.failType },
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

        const a = createSubmitAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })(f.body);
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

        const a = createSubmitAction({
            failType: f.failType,
            networkFailType: f.networkFailType,
            okType: f.okType,
            sendType: f.sendType,
            url: URL,
        })(f.body);
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
