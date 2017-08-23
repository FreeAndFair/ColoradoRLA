import {
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from '../action/countyDashboardRefresh';
import fetchAuditBoardAsmState from '../action/fetchAuditBoardAsmState';
import fetchCountyAsmState from '../action/fetchCountyAsmState';

import notice from '../notice';


function* countyLoginOk() {
    countyDashboardRefresh();
    fetchAuditBoardAsmState();
    fetchCountyAsmState();

    yield put({ type: 'COUNTY_POLL' });
}

function* countyLoginFail(): IterableIterator<void> {
    notice.danger('Invalid credentials.');
}

function* countyLoginNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to log in due to network error.');
}


export default function* countyLoginSaga() {
    yield takeLatest('COUNTY_LOGIN_FAIL', countyLoginFail);
    yield takeLatest('COUNTY_LOGIN_NETWORK_FAIL', countyLoginNetworkFail);
    yield takeLatest('COUNTY_LOGIN_OK', countyLoginOk);
}
