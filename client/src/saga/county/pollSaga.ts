import { delay } from 'redux-saga';
import {
    call,
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from 'corla/action/county/dashboardRefresh';
import fetchAuditBoardAsmState from 'corla/action/county/fetchAuditBoardAsmState';
import countyFetchContests from 'corla/action/county/fetchContests';
import fetchCountyAsmState from 'corla/action/county/fetchCountyAsmState';


function* countyPoll() {
    const COUNTY_POLL_DELAY = 1000 * 5;

    const { county, dashboard, loggedIn } = yield select();

    if (!loggedIn) { return null; }
    if (dashboard !== 'county') { return null; }

    yield delay(COUNTY_POLL_DELAY);

    countyDashboardRefresh();
    fetchAuditBoardAsmState();
    fetchCountyAsmState();

    if (county && county.id) {
        countyFetchContests(county.id);
    }

    yield put({ type: 'COUNTY_POLL' });
}


export default function* countyPollSaga() {
    yield takeLatest('COUNTY_POLL', countyPoll);
}
