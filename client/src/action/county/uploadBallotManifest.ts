import action from 'corla/action';
import { endpoint } from 'corla/config';
import { empty } from 'corla/util';

const importUrl = endpoint('import-ballot-manifest');
const uploadUrl = endpoint('upload-file');


function createFormData(file: Blob, hash: string): FormData {
    const formData = new FormData();

    formData.append('file', file);
    formData.append('hash', hash);

    return formData;
}

async function importBallotManifest(body: JSON.UploadBallotManifestOk) {
    const init: RequestInit = {
        body: JSON.stringify(body),
        credentials: 'include',
        method: 'post',
    };

    try {
        action('IMPORT_BALLOT_MANIFEST_SEND');

        const r = await fetch(importUrl, init);

        const received = await r.json().catch(empty);
        const sent = body;
        const data = { received, sent };

        if (!r.ok) {
            action('IMPORT_BALLOT_MANIFEST_FAIL', data);
            return;
        }

        action('IMPORT_BALLOT_MANIFEST_OK', data);
    } catch (e) {
        action('IMPORT_BALLOT_MANIFEST_NETWORK_FAIL');

        throw e;
    }
}

async function uploadBallotManifest(countyId: number, file: Blob, hash: string) {
    const formData = createFormData(file, hash);

    const init: RequestInit = {
        body: formData,
        credentials: 'include',
        method: 'post',
    };

    try {
        action('UPLOAD_BALLOT_MANIFEST_SEND');

        const r = await fetch(uploadUrl, init);

        const received = await r.json().catch(empty);
        const sent = { file, hash };
        const data = { received, sent };

        if (!r.ok) {
            action('UPLOAD_BALLOT_MANIFEST_FAIL', data);
            return;
        }

        action('UPLOAD_BALLOT_MANIFEST_OK', data);

        importBallotManifest(received);
    } catch (e) {
        action('UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL');

        throw e;
    }

}


export default uploadBallotManifest;
