import { delay } from 'redux-saga';
import { all, cancel, fork, take, takeLatest } from 'redux-saga/effects';

import createPollSaga from 'corla/saga/createPollSaga';

import dashboardRefresh from 'corla/action/dos/dashboardRefresh';
import fetchContests from 'corla/action/dos/fetchContests';


const DOS_POLL_DELAY = 1000 * 5;

const dosDashboardPollSaga = createPollSaga(
    [dashboardRefresh, fetchContests],
    DOS_POLL_DELAY,
    'DOS_DASHBOARD_POLL_START',
    'DOS_DASHBOARD_POLL_STOP',
);


export default function* pollSaga() {
    yield all([
        dosDashboardPollSaga(),
    ]);
}
