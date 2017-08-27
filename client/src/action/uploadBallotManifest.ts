import action from '.';

import { endpoint } from '../config';

const importUrl = endpoint('import-ballot-manifest');
const uploadUrl = endpoint('upload-file');


function createFormData(file: Blob, hash: string) {
    const formData = new FormData();

    formData.append('file', file);
    formData.append('hash', hash);

    return formData;
}

async function importBallotManifest(body: any) {
    const init: any = {
        body: JSON.stringify(body),
        credentials: 'include',
        method: 'post',
    };

    try {
        action('IMPORT_BALLOT_MANIFEST_SEND');

        const r = await fetch(importUrl, init);

        const received = await r.json();
        const sent = body;
        const data = { received, sent };

        if (!r.ok) {
            action('IMPORT_BALLOT_MANIFEST_FAIL', data);
            return;
        }

        action('IMPORT_BALLOT_MANIFEST_OK', data);
    } catch (e) {
        if (e.message === 'Failed to fetch') {
            action('IMPORT_BALLOT_MANIFEST_NETWORK_FAIL');
        }

        action('INTERNAL_ERROR');

        throw e;
    }
}

async function uploadBallotManifest(countyId: number, file: Blob, hash: string) {
    const formData = createFormData(file, hash);

    const init: any = {
        body: formData,
        credentials: 'include',
        method: 'post',
    };

    try {
        action('UPLOAD_BALLOT_MANIFEST_SEND');

        const r = await fetch(uploadUrl, init);

        const received = await r.json();
        const sent = { file, hash };
        const data = { received, sent };

        if (!r.ok) {
            action('UPLOAD_BALLOT_MANIFEST_FAIL', data);
            return;
        }

        action('UPLOAD_BALLOT_MANIFEST_OK', data);

        importBallotManifest(received);
    } catch (e) {
        if (e.message === 'Failed to fetch') {
            action('UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL');
        }

        action('INTERNAL_ERROR');

        throw e;
    }

}


export default uploadBallotManifest;
