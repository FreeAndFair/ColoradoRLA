import * as _ from 'lodash';

import { all, select, takeLatest } from 'redux-saga/effects';

import * as config from 'corla/config';

import createPollSaga from 'corla/saga/createPollSaga';

import dashboardRefresh from 'corla/action/county/dashboardRefresh';
import fetchAuditBoardAsmState from 'corla/action/county/fetchAuditBoardAsmState';
import fetchContests from 'corla/action/county/fetchContests';
import fetchCountyAsmState from 'corla/action/county/fetchCountyAsmState';


const COUNTY_POLL_DELAY = config.pollDelay;

function* auditPoll() {
    const { county } = yield select();

    const currentState = _.get(county, 'asm.auditBoard.currentState');
    const shouldSync = currentState === 'WAITING_FOR_ROUND_START'
        || currentState === 'WAITING_FOR_ROUND_START_NO_AUDIT_BOARD';

    if (shouldSync) {
        dashboardRefresh();
        fetchAuditBoardAsmState();
        fetchCountyAsmState();
    }
}

const auditPollSaga = createPollSaga(
    [auditPoll],
    COUNTY_POLL_DELAY,
    'COUNTY_AUDIT_POLL_START',
    'COUNTY_AUDIT_POLL_STOP',
);

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

const dashboardPollSaga = createPollSaga(
    [dashboardPoll],
    COUNTY_POLL_DELAY,
    'COUNTY_DASHBOARD_POLL_START',
    'COUNTY_DASHBOARD_POLL_STOP',
);


export default function* pollSaga() {
    yield all([
        auditPollSaga(),
        boardSignInSaga(),
        dashboardPollSaga(),
    ]);
}
