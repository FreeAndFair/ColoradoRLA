import { takeEvery } from 'redux-saga/effects';

import notice from 'corla/notice';


function* uploadAcvrOk(): any {
    notice.ok('Audit Board interpretations recorded.');
}

function* uploadAcvrFail(): any {
    notice.danger('Failed to record Audit Board interpretations.');
}

function* uploadAcvrNetworkFail(): any {
    notice.danger('Network error: failed to record Audit Board interpretations.');
}


export default function* fileUploadSaga() {
    yield takeEvery('UPLOAD_ACVR_OK', uploadAcvrOk);
    yield takeEvery('UPLOAD_ACVR_FAIL', uploadAcvrFail);
    yield takeEvery('UPLOAD_ACVR_NETWORK_FAIL', uploadAcvrNetworkFail);
}
