import {
    put,
    takeLatest,
} from 'redux-saga/effects';

import dosDashboardRefresh from '../action/dosDashboardRefresh';
import dosFetchAsmState from '../action/dosFetchAsmState';
import dosFetchContests from '../action/dosFetchContests';

import notice from '../notice';


function* dosLoginOk() {
    dosDashboardRefresh();
    dosFetchAsmState();
    dosFetchContests();

    yield put({ type: 'DOS_POLL' });
}

function* dosLoginFail(): IterableIterator<void> {
    notice.danger('Invalid credentials.');
}

function* dosLoginNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to log in due to network error.');
}


export default function* dosLoginSaga() {
    yield takeLatest('DOS_LOGIN_FAIL', dosLoginFail);
    yield takeLatest('DOS_LOGIN_NETWORK_FAIL', dosLoginNetworkFail);
    yield takeLatest('DOS_LOGIN_OK', dosLoginOk);
}
