import { takeLatest } from 'redux-saga/effects';

import notice from 'corla/notice';


function* signInOk(): IterableIterator<void> {
    notice.ok('Audit board signed in.');
}

function* signOutOk(): IterableIterator<void> {
    notice.ok('Audit board signed out.');
}


export default function* auditBoardSignInSaga() {
    yield takeLatest('AUDIT_BOARD_SIGN_IN_OK', signInOk);
    yield takeLatest('AUDIT_BOARD_SIGN_OUT_OK', signOutOk);
}
