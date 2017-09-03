import {
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyFetchContests from 'corla/action/countyFetchContests';
import countyFetchCvr from 'corla/action/countyFetchCvr';
import fetchCvrsToAudit from 'corla/action/fetchCvrsToAudit';

import { parse } from 'corla/adapter/countyDashboardRefresh';


function* countyRefreshOk({ data }: any): any {
    const state = yield select();

    const county = parse(data, state);

    if (county.id) {
        countyFetchContests(county.id);
    }

    if (county.ballotUnderAuditId) {
        countyFetchCvr(county.ballotUnderAuditId);
    }

    if (county.currentRound) {
        fetchCvrsToAudit(county.currentRound);
    }
}

export default function* dosLoginSaga() {
    yield takeLatest('COUNTY_DASHBOARD_REFRESH_OK', countyRefreshOk);
}
