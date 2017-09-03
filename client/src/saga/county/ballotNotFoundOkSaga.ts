import { takeLatest } from 'redux-saga/effects';

import notice from 'corla/notice';

import countyDashboardRefresh from 'corla/action/county/dashboardRefresh';


function* ballotNotFoundOk(): any {
    notice.ok('Previous ballot recorded as Not Found.');
    countyDashboardRefresh();
}

export default function* ballotNotFoundOkSaga() {
    yield takeLatest('BALLOT_NOT_FOUND_OK', ballotNotFoundOk);
}
