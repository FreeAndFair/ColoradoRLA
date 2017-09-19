import { delay } from 'redux-saga';
import { takeLatest } from 'redux-saga/effects';

import dashboardRefresh from 'corla/action/dos/dashboardRefresh';
import fetchAsmState from 'corla/action/dos/fetchAsmState';
import fetchContests from 'corla/action/dos/fetchContests';


const DOS_POLL_DELAY = 1000 * 5;

function* pollTask() {
    while (true) {
        dashboardRefresh();
        fetchAsmState();
        fetchContests();

        yield delay(DOS_POLL_DELAY);
    }
}


export default function* pollSaga() {
    yield takeLatest('DOS_DASHBOARD_POLL', pollTask);
}
