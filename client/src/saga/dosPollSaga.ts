import {
    call,
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import dosDashboardRefresh from '../action/dosDashboardRefresh';
import dosFetchContests from '../action/dosFetchContests';


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


export default function* dosPollSaga() {
    yield takeLatest('DOS_POLL', dosPoll);
}
