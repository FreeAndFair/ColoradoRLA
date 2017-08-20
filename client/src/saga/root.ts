import {
    all,
    call,
    put,
    select,
    takeEvery,
    takeLatest,
} from 'redux-saga/effects';

import dosDashboardRefresh from '../action/dosDashboardRefresh';
import dosFetchContests from '../action/dosFetchContests';


function* debugSaga() {
    yield takeEvery('*', (a: any) => {
        // tslint:disable
        console.log('[debug]', a.type, a);
        // tslint:enable
    });
}

function* dosLoginOk() {
    dosDashboardRefresh();
    dosFetchContests();

    yield put({ type: 'DOS_POLL' });
}

function* dosLoginSaga() {
    yield takeEvery('DOS_LOGIN_OK', dosLoginOk);
}

function delay(t: number) {
    return new Promise(r => {
        setTimeout(() => r(true), t);
    });
}

function* dosPoll() {
    const DOS_POLL_DELAY = 1000 * 5;

    const { dashboard, loggedIn } = yield select();

    if (!loggedIn) { return null; }
    if (dashboard !== 'sos') { return null; }

    yield call(delay, DOS_POLL_DELAY);

    dosDashboardRefresh();
    dosFetchContests();

    yield put({ type: 'DOS_POLL' });
}

function* dosPollSaga() {
    yield takeLatest('DOS_POLL', dosPoll);
}


export default function* rootSaga() {
    yield all([
        debugSaga(),
        dosLoginSaga(),
        dosPollSaga(),
    ]);
}