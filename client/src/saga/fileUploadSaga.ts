import { takeEvery } from 'redux-saga/effects';

import notice from '../notice';


function* uploadBallotManifestOk(action: any): any {
    const { data } = action;
    const { sent } = data;

    notice.ok(`Uploaded ballot manifest "${sent.file.name}".`);
}

function* uploadBallotManifestFail(action: any): any {
    const { body, status } = action.data.received;

    switch (body.result) {
    // case 'hash mismatch': {
    //     notice.danger('Failed to upload ballot manifest: hash mismatch.');
    //     notice.warning('Please verify that the entered hash matches the file.');
    //     return;
    // }
    // case 'malformed ballot manifest file': {
    //     notice.danger('Failed to upload ballot manifest: malformed file.');
    //     notice.warning('Please verify that the uploaded file is valid ballot manifest.');
    //     return;
    // }
    default: {
        notice.danger('Failed to upload ballot manifest.');
        notice.warning('Please verify that the uploaded file is a valid ballot manifest with a matching hash.');
        return;
    }
    }
}

function* uploadBallotManifestNetworkFail(): any {
    notice.danger('Network error: failed to upload ballot manifest.');
}

function* uploadCvrExportOk(action: any): any {
    const { data } = action;
    const { sent } = data;

    notice.ok(`Uploaded CVR export "${sent.file.name}".`);
}

function* uploadCvrExportFail(action: any): any {
    const { body, status } = action.data.received;

    switch (body.result) {
    // case 'hash mismatch': {
    //     notice.danger('Failed to upload CVR export: hash mismatch.');
    //     notice.warning('Please verify that the entered hash matches the file.');
    //     return;
    // }
    // case 'malformed CVR export file': {
    //     notice.danger('Failed to upload CVR export: malformed file.');
    //     notice.warning('Please verify that the uploaded file is a valid CVR export.');
    //     return;
    // }
    default: {
        notice.danger('Failed to upload CVR export.');
        notice.warning('Please verify that the uploaded file is a valid CVR export with a matching hash.');
        return;
    }
    }
}

function* uploadCvrExportNetworkFail(): any {
    notice.danger('Network error: failed to upload ballot manifest.');
}

function* uploadAcvrOk(): any {
    notice.ok('Uploaded ACVR.');
}

function* uploadAcvrFail(): any {
    notice.danger('Failed to upload ACVR.');
}

function* uploadAcvrNetworkFail(): any {
    notice.danger('Network error: failed to upload ACVR.');
}


export default function* fileUploadSaga() {
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_OK', uploadBallotManifestOk);
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_FAIL', uploadBallotManifestFail);
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL', uploadBallotManifestNetworkFail);


    yield takeEvery('UPLOAD_CVR_EXPORT_OK', uploadCvrExportOk);
    yield takeEvery('UPLOAD_CVR_EXPORT_FAIL', uploadCvrExportFail);
    yield takeEvery('UPLOAD_CVR_EXPORT_NETWORK_FAIL', uploadCvrExportNetworkFail);

    yield takeEvery('UPLOAD_ACVR_OK', uploadAcvrOk);
    yield takeEvery('UPLOAD_ACVR_FAIL', uploadAcvrFail);
    yield takeEvery('UPLOAD_ACVR_NETWORK_FAIL', uploadAcvrNetworkFail);
}
