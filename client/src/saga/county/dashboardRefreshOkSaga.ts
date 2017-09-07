import {
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyFetchContests from 'corla/action/county/fetchContests';
import fetchCvrsToAudit from 'corla/action/county/fetchCvrsToAudit';

import { parse } from 'corla/adapter/countyDashboardRefresh';

import currentBallotIdSelector from 'corla/selector/county/currentBallotId';


function* countyRefreshOk({ data }: any): any {
    const state = yield select();

    const county = parse(data, state);

    if (county.id) {
        countyFetchContests(county.id);
    }

    if (county.currentRound) {
        fetchCvrsToAudit(county.currentRound);
    }
}

export default function* dosLoginSaga() {
    yield takeLatest('COUNTY_DASHBOARD_REFRESH_OK', countyRefreshOk);
}
