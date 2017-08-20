import { delay } from 'redux-saga';
import {
    call,
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyDashboardRefresh from '../action/countyDashboardRefresh';
import countyFetchContests from '../action/countyFetchContests';


function* countyPoll() {
    const COUNTY_POLL_DELAY = 1000 * 5;

    const { county, dashboard, loggedIn } = yield select();

    if (!loggedIn) { return null; }
    if (dashboard !== 'county') { return null; }

    yield delay(COUNTY_POLL_DELAY);

    countyDashboardRefresh();

    if (county && county.id) {
        countyFetchContests(county.id);
    }

    yield put({ type: 'COUNTY_POLL' });
}


export default function* countyPollSaga() {
    yield takeLatest('COUNTY_POLL', countyPoll);
}
