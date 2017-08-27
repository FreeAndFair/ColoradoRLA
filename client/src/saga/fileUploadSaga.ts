import { takeEvery } from 'redux-saga/effects';

import notice from '../notice';


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
    yield takeEvery('UPLOAD_ACVR_OK', uploadAcvrOk);
    yield takeEvery('UPLOAD_ACVR_FAIL', uploadAcvrFail);
    yield takeEvery('UPLOAD_ACVR_NETWORK_FAIL', uploadAcvrNetworkFail);
}
