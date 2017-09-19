import { all, takeLatest } from 'redux-saga/effects';

import createPollSaga from 'corla/saga/createPollSaga';

import dashboardRefresh from 'corla/action/dos/dashboardRefresh';
import fetchContests from 'corla/action/dos/fetchContests';


function* countyOverviewSaga() {
    yield takeLatest('DOS_COUNTY_OVERVIEW_SYNC', () => dashboardRefresh());
}

const DOS_POLL_DELAY = 1000 * 5;

const dashboardPollSaga = createPollSaga(
    [dashboardRefresh, fetchContests],
    DOS_POLL_DELAY,
    'DOS_DASHBOARD_POLL_START',
    'DOS_DASHBOARD_POLL_STOP',
);

function* defineAuditSaga() {
    yield takeLatest('DOS_DEFINE_AUDIT_SYNC', () => dashboardRefresh());
}

const selectContestsPollSaga = createPollSaga(
    [dashboardRefresh, fetchContests],
    DOS_POLL_DELAY,
    'DOS_SELECT_CONTESTS_POLL_START',
    'DOS_SELECT_CONTESTS_POLL_STOP',
);

function* randomSeedSaga() {
    yield takeLatest('DOS_DEFINE_AUDIT_RANDOM_SEED_SYNC', () => dashboardRefresh());
}

function* defineAuditReviewSaga() {
    yield takeLatest('DOS_DEFINE_AUDIT_REVIEW_SYNC', () => dashboardRefresh());
}


export default function* pollSaga() {
    yield all([
        countyOverviewSaga(),
        dashboardPollSaga(),
        defineAuditReviewSaga(),
        defineAuditSaga(),
        randomSeedSaga(),
        selectContestsPollSaga(),
    ]);
}
