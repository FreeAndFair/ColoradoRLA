import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import uploadRandomSeed from './uploadRandomSeed';


const setup = () => {
    const seed = 'deadbeef';

    const store = configureStore([thunkMiddleware])({});

    return {
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
        seed,
        store,
    };
};

const teardown = (f: any) => {
    fetch.restore();
    f.store.clearActions();
};


const URL = /\/upload-random-seed$/;


test('uploadRandomSeed', s => {
    test('sends the right data', t => {
        const f = setup();
        const body = { result: 'OK' };
        f.fetch(URL, { body, status: 200 });

        t.plan(3);

        const a = uploadRandomSeed(f.seed);
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];

        t.equal(args.method, 'post');
        t.deepEqual(args.body, { seed: f.seed });

        teardown(f);
    });

    test('when upload ok', t => {
        const f = setup();
        const body = { result: 'Random seed set' };
        f.fetch(URL, { body, status: 200 });

        t.plan(1);

        const a = uploadRandomSeed(f.seed);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_RANDOM_SEED_SEND' },
                { type: 'UPLOAD_RANDOM_SEED_OK', data: body },
            ]);

            teardown(f);
        });
    });

    test('when server error', t => {
        const f = setup();
        f.fetch(URL, {
            body: { result: 'Could not set random seed' },
            status: 500,
        });

        t.plan(1);

        const a = uploadRandomSeed(f.seed);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_RANDOM_SEED_SEND' },
                { type: 'UPLOAD_RANDOM_SEED_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when bad request', t => {
        const f = setup();
        f.fetch(URL, {
            body: { result: 'Invalid random seed specified' },
            status: 400,
        });

        t.plan(1);

        const a = uploadRandomSeed(f.seed);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_RANDOM_SEED_SEND' },
                { type: 'UPLOAD_RANDOM_SEED_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = uploadRandomSeed(f.seed);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_RANDOM_SEED_SEND' },
                { type: 'UPLOAD_RANDOM_SEED_NETWORK_FAIL' },
            ]);

            teardown(f);
        });
    });

    s.end();
});
