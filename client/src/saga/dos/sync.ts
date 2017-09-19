import { all, takeLatest } from 'redux-saga/effects';

import createPollSaga from 'corla/saga/createPollSaga';

import dashboardRefresh from 'corla/action/dos/dashboardRefresh';
import fetchContests from 'corla/action/dos/fetchContests';


const DOS_POLL_DELAY = 1000 * 5;

const dashboardPollSaga = createPollSaga(
    [dashboardRefresh, fetchContests],
    DOS_POLL_DELAY,
    'DOS_DASHBOARD_POLL_START',
    'DOS_DASHBOARD_POLL_STOP',
);

function* defineAuditSync(): IterableIterator<void> {
    dashboardRefresh();
}

function* defineAuditSaga() {
    yield takeLatest('DOS_DEFINE_AUDIT_SYNC', defineAuditSync);
}

const selectContestsPollSaga = createPollSaga(
    [dashboardRefresh, fetchContests],
    DOS_POLL_DELAY,
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
);


export default function* pollSaga() {
    yield all([
        dashboardPollSaga(),
        defineAuditSaga(),
        selectContestsPollSaga(),
    ]);
}
