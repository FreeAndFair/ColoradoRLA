import { takeLatest } from 'redux-saga/effects';

import countyDashboardRefresh from '../action/countyDashboardRefresh';


function* ballotNotFoundOk(): any {
    countyDashboardRefresh();
}

export default function* ballotNotFoundOkSaga() {
    yield takeLatest('BALLOT_NOT_FOUND_OK', ballotNotFoundOk);
}
