import { delay } from 'redux-saga';
import { all, cancel, fork, take, takeLatest } from 'redux-saga/effects';

import dashboardRefresh from 'corla/action/dos/dashboardRefresh';
import fetchContests from 'corla/action/dos/fetchContests';


const DOS_POLL_DELAY = 1000 * 5;

function* dosDashboardPoll() {
    while (true) {
        dashboardRefresh();
        fetchContests();

        yield delay(DOS_POLL_DELAY);
    }
}

function* dosDashboardPollSaga() {
    let poll = yield take('DOS_DASHBOARD_POLL_START')

    while (poll) {
        const task = yield fork(dosDashboardPoll);

        yield take('DOS_DASHBOARD_POLL_STOP');

        yield cancel(task);

        poll = yield take('DOS_DASHBOARD_POLL_START')
    }
}


export default function* pollSaga() {
    yield all([
        dosDashboardPollSaga(),
    ]);
}
