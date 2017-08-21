import {
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyFetchContests from '../action/countyFetchContests';
import countyFetchCvr from '../action/countyFetchCvr';

import { parse } from '../adapter/countyDashboardRefresh';


function* countyRefreshOk({ data }: any): any {
    const state = yield select();

    const county = parse(data, state);

    if (county.id) {
        countyFetchContests(county.id);
    }

    if (county.ballotUnderAuditId) {
        countyFetchCvr(county.ballotUnderAuditId);
    }
}

export default function* dosLoginSaga() {
    yield takeLatest('COUNTY_DASHBOARD_REFRESH_OK', countyRefreshOk);
}
