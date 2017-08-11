import * as sinon from 'sinon';
import * as test from 'tape';

import * as fetch from 'fetch-mock';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

import uploadCvrExport from './uploadCvrExport';


const setup = () => {
    const countyId = '1234';
    const file = new Blob();
    const hash = 'deadbeef';

    const formData = new FormData();
    formData.append('county', countyId);
    formData.append('cvr_file', file);
    formData.append('hash', hash);

    const store = configureStore([thunkMiddleware])({});

    return {
        countyId,
        fetch: (m: any, r: any) =>
            fetch.mock(m, r).catch(500),
        file,
        formData,
        hash,
        store,
    };
};

const teardown = (f: any) => {
    fetch.restore();
    f.store.clearActions();
};


const URL = /\/upload-cvr-export$/;


test('uploadCvrExport', s => {
    test('sends the right form data', t => {
        const f = setup();
        f.fetch(URL, { body: 'OK', status: 200 });

        t.plan(5);

        const a = uploadCvrExport(f.countyId, f.file, f.hash);
        a(sinon.stub());

        t.equal(fetch.calls().matched.length, 1, 'fetched to the URL');

        const args: any = fetch.lastCall()[1];

        t.equal(args.method, 'post');

        t.equal(args.body.get('county'), f.formData.get('county'));
        t.deepEqual(args.body.get('cvr_file'), f.formData.get('cvr_file'));
        t.equal(args.body.get('hash'), f.formData.get('hash'));

        teardown(f);
    });

    test('when upload ok', t => {
        const f = setup();
        f.fetch(URL, { body: 'OK', status: 200 });

        t.plan(1);

        const a = uploadCvrExport(f.countyId, f.file, f.hash);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_CVR_EXPORT_SEND' },
                { type: 'UPLOAD_CVR_EXPORT_RECEIVE' },
            ]);

            teardown(f);
        });
    });

    test('when upload failed', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Malformed CVR Export File',
            status: 422,
        });

        t.plan(1);

        const a = uploadCvrExport(f.countyId, f.file, f.hash);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_CVR_EXPORT_SEND' },
                { type: 'UPLOAD_CVR_EXPORT_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when bad request', t => {
        const f = setup();
        f.fetch(URL, {
            body: 'Bad Request',
            status: 400,
        });

        t.plan(1);

        const a = uploadCvrExport(f.countyId, f.file, f.hash);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_CVR_EXPORT_SEND' },
                { type: 'UPLOAD_CVR_EXPORT_FAIL' },
            ]);

            teardown(f);
        });
    });

    test('when network failure', t => {
        const f = setup();
        f.fetch(URL, { throws: 'Network error' });

        t.plan(1);

        const a = uploadCvrExport(f.countyId, f.file, f.hash);
        f.store.dispatch(a);

        fetch.flush().then(() => {
            t.deepEqual(f.store.getActions(), [
                { type: 'UPLOAD_CVR_EXPORT_SEND' },
                { type: 'UPLOAD_CVR_EXPORT_NETWORK_FAIL' },
            ]);

            teardown(f);
        });
    });

    s.end();
});
