import { takeEvery } from 'redux-saga/effects';

import notice from '../notice';


function* uploadBallotManifestOk({ sent }: any): any {
    notice.ok(`Uploaded ballot manifest "${sent.file.name}".`);
}

function* uploadBallotManifestFail(): any {
    notice.danger('Failed to upload ballot manifest');
}

function* uploadBallotManifestNetworkFail(): any {
    notice.danger('Network error: failed to upload ballot manifest.');
}

function* uploadCvrExportOk({ sent }: any): any {
    notice.ok(`Uploaded CVR export "${sent.file.name}".`);
}

function* uploadCvrExportFail(): any {
    notice.danger('Failed to upload CVR export.');
}

function* uploadCvrExportNetworkFail(): any {
    notice.danger('Network error: failed to upload ballot manifest.');
}

export default function* fileUploadSaga() {
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_OK', uploadBallotManifestOk);
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_FAIL', uploadBallotManifestFail);
    yield takeEvery('UPLOAD_BALLOT_MANIFEST_NETWORK_FAIL', uploadBallotManifestNetworkFail);


    yield takeEvery('UPLOAD_CVR_EXPORT_OK', uploadCvrExportOk);
    yield takeEvery('UPLOAD_CVR_EXPORT_FAIL', uploadCvrExportFail);
    yield takeEvery('UPLOAD_CVR_EXPORT_NETWORK_FAIL', uploadCvrExportNetworkFail);
}
