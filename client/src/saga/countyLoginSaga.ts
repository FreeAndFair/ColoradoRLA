import {
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from '../action/countyDashboardRefresh';
import fetchAuditBoardAsmState from '../action/fetchAuditBoardAsmState';
import fetchCountyAsmState from '../action/fetchCountyAsmState';


function* countyLoginOk() {
    countyDashboardRefresh();
    fetchAuditBoardAsmState();
    fetchCountyAsmState();

    yield put({ type: 'COUNTY_POLL' });
}

export default function* dosLoginSaga() {
    yield takeLatest('COUNTY_LOGIN_OK', countyLoginOk);
}
