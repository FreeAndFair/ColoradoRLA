import { all, select, takeLatest } from 'redux-saga/effects';

import createPollSaga from 'corla/saga/createPollSaga';

import dashboardRefresh from 'corla/action/county/dashboardRefresh';
import fetchAuditBoardAsmState from 'corla/action/county/fetchAuditBoardAsmState';
import fetchContests from 'corla/action/county/fetchContests';
import fetchCountyAsmState from 'corla/action/county/fetchCountyAsmState';


function* boardSignInSaga() {
    yield takeLatest('COUNTY_BOARD_SIGN_IN_SYNC', () => {
        dashboardRefresh();
        fetchAuditBoardAsmState();
        fetchCountyAsmState();
    });
}

function* dashboardPoll() {
    dashboardRefresh();
    fetchAuditBoardAsmState();
    fetchCountyAsmState();

    const { county } = yield select();

    if (county && county.id) {
        fetchContests(county.id);
    }
}

const COUNTY_POLL_DELAY = 1000 * 5;

const dashboardPollSaga = createPollSaga(
    [dashboardPoll],
    COUNTY_POLL_DELAY,
    'COUNTY_DASHBOARD_POLL_START',
    'COUNTY_DASHBOARD_POLL_STOP',
);


export default function* pollSaga() {
    yield all([
        boardSignInSaga(),
        dashboardPollSaga(),
    ]);
}
