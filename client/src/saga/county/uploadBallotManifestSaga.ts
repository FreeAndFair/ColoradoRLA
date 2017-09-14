import {
    put,
    select,
    takeEvery,
} from 'redux-saga/effects';

import notice from 'corla/notice';


function* importBallotManifestOk(action: any): IterableIterator<any> {
    const { data } = action;
    const { sent } = data;

    notice.ok(`Imported ballot manifest "${sent.filename}".`);
}

function* importBallotManifestFail(action: any): IterableIterator<any> {
    const { data } = action;
    const { received, sent } = data;

    switch (sent.hash_status) {
    case 'MISMATCH': {
        notice.danger('Failed to import ballot manifest.');
        notice.warning('Please verify that the hash matches the file to be uploaded.');
        break;
    }
    default: {
        notice.danger('Failed to import ballot manifest.');
        notice.warning('Please verify that the uploaded file is a valid ballot manifest.');
        break;
    }
    }
}

function* importBallotManifestNetworkFail(): IterableIterator<any> {
    notice.danger('Network error: failed to upload ballot manifest.');
}

function* uploadBallotManifestOk(action: any): IterableIterator<any> {
    const { data } = action;
    const { received } = data;

    notice.ok(`Uploaded ballot manifest "${received.filename}".`);
}

function* uploadBallotManifestFail(action: any): IterableIterator<any> {
    notice.danger('Failed to upload ballot manifest.');
}

function* uploadBallotManifestNetworkFail(): IterableIterator<any> {
    notice.danger('Network error: failed to upload ballot manifest.');
}

function createUploadingBallotManifest(uploading: boolean) {
    function* uploadingBallotManifest(action: any): IterableIterator<any> {
        const data = { uploading };

        yield put({ data, type: 'UPLOADING_BALLOT_MANIFEST' });
    }

    return uploadingBallotManifest;
}

const UPLOADING_FALSE = [
    'IMPORT_BALLOT_MANIFEST_FAIL',
    'IMPORT_BALLOT_MANIFEST_NETWORK_FAIL',
    'IMPORT_BALLOT_MANIFEST_OK',
    'UPLOAD_BALLOT_MANIFEST_FAIL',
    'UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL',
];

const UPLOADING_TRUE = [
    'UPLOAD_BALLOT_MANIFEST_SEND',
];


export default function* fileUploadSaga() {
    yield takeEvery('IMPORT_BALLOT_MANIFEST_OK', importBallotManifestOk);
    yield takeEvery('IMPORT_BALLOT_MANIFEST_FAIL', importBallotManifestFail);
    yield takeEvery('IMPORT_BALLOT_MANIFEST_NETWORK_FAIL', importBallotManifestNetworkFail);

    yield takeEvery('UPLOAD_BALLOT_MANIFEST_OK', uploadBallotManifestOk);
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_FAIL', uploadBallotManifestFail);
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL', uploadBallotManifestNetworkFail);

    yield takeEvery(UPLOADING_FALSE, createUploadingBallotManifest(false));
    yield takeEvery(UPLOADING_TRUE, createUploadingBallotManifest(true));
}
