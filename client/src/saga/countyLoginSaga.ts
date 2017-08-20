import {
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from '../action/countyDashboardRefresh';


function* countyLoginOk() {
    countyDashboardRefresh();

    yield put({ type: 'COUNTY_POLL' });
}

export default function* dosLoginSaga() {
    yield takeLatest('COUNTY_LOGIN_OK', countyLoginOk);
}
