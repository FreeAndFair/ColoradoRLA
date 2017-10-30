import * as _ from 'lodash';

import { all, select, takeLatest } from 'redux-saga/effects';

import * as config from 'corla/config';

import createPollSaga from 'corla/saga/createPollSaga';

import dashboardRefresh from 'corla/action/county/dashboardRefresh';
import fetchAuditBoardASMState from 'corla/action/county/fetchAuditBoardASMState';
import fetchContests from 'corla/action/county/fetchContests';
import fetchCountyASMState from 'corla/action/county/fetchCountyASMState';

import cvrExportUploadingSelector from 'corla/selector/county/cvrExportUploading';


const COUNTY_POLL_DELAY = config.pollDelay;

function* auditPoll() {
    const countyState = yield select();

    const asmState = countyState.asm.auditBoard;
    const shouldSync = asmState === 'WAITING_FOR_ROUND_START'
        || asmState === 'WAITING_FOR_ROUND_START_NO_AUDIT_BOARD';

    if (shouldSync) {
        dashboardRefresh();
        fetchAuditBoardASMState();
        fetchCountyASMState();
    }
}

const auditPollSaga = createPollSaga(
    [auditPoll],
    'COUNTY_AUDIT_POLL_START',
    'COUNTY_AUDIT_POLL_STOP',
    () => COUNTY_POLL_DELAY,
);

function* boardSignInSaga() {
    yield takeLatest('COUNTY_BOARD_SIGN_IN_SYNC', () => {
        dashboardRefresh();
        fetchAuditBoardASMState();
        fetchCountyASMState();
    });
}

function* dashboardPoll() {
    dashboardRefresh();
    fetchAuditBoardASMState();
    fetchCountyASMState();

    const countyState = yield select();

    if (countyState && !_.isNil(countyState.id)) {
        fetchContests(countyState.id);
    }
}

function* selectPollDelay() {
    const countyState = yield select();

    const isUploading = cvrExportUploadingSelector(countyState);

    const delay = isUploading ? 5000 : COUNTY_POLL_DELAY;

    return delay;
}

const dashboardPollSaga = createPollSaga(
    [dashboardPoll],
    'COUNTY_DASHBOARD_POLL_START',
    'COUNTY_DASHBOARD_POLL_STOP',
    selectPollDelay,
);


export default function* pollSaga() {
    yield all([
        auditPollSaga(),
        boardSignInSaga(),
        dashboardPollSaga(),
    ]);
}
