import {
    put,
    select,
    takeEvery,
} from 'redux-saga/effects';

import countyDashboardRefresh from '../action/countyDashboardRefresh';


function* countyLoginOk() {
    countyDashboardRefresh();

    yield put({ type: 'COUNTY_POLL' });
}

export default function* dosLoginSaga() {
    yield takeEvery('COUNTY_LOGIN_OK', countyLoginOk);
}
