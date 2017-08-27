import { takeEvery } from 'redux-saga/effects';

import notice from '../../notice';


function* importBallotManifestOk(action: any): IterableIterator<any> {
    const { data } = action;
    const { received } = data;

    notice.ok(`Imported ballot manifest "${received.filename}".`);
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


export default function* fileUploadSaga() {
    yield takeEvery('NEXT_IMPORT_BALLOT_MANIFEST_OK', importBallotManifestOk);
    yield takeEvery('NEXT_IMPORT_BALLOT_MANIFEST_FAIL', importBallotManifestFail);
    yield takeEvery('NEXT_IMPORT_BALLOT_MANIFEST_NETWORK_FAIL', importBallotManifestNetworkFail);

    yield takeEvery('NEXT_UPLOAD_BALLOT_MANIFEST_OK', uploadBallotManifestOk);
    yield takeEvery('NEXT_UPLOAD_BALLOT_MANIFEST_FAIL', uploadBallotManifestFail);
    yield takeEvery('NEXT_UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL', uploadBallotManifestNetworkFail);
}
