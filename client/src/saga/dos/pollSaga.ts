import { delay } from 'redux-saga';
import { cancel, fork, take, takeLatest } from 'redux-saga/effects';

import dashboardRefresh from 'corla/action/dos/dashboardRefresh';
import fetchContests from 'corla/action/dos/fetchContests';


const DOS_POLL_DELAY = 1000 * 5;

function* pollTask() {
    while (true) {
        dashboardRefresh();
        fetchContests();

        yield delay(DOS_POLL_DELAY);
    }
}


export default function* pollSaga() {
    let poll = yield take('DOS_DASHBOARD_POLL_START')

    while (poll) {
        const t = yield fork(pollTask);

        yield take('DOS_DASHBOARD_POLL_STOP');

        yield cancel(t);

        poll = yield take('DOS_DASHBOARD_POLL_START')
    }
}
