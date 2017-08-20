import { all } from 'redux-saga/effects';

import countyDashboardRefreshOkSaga from './countyDashboardRefreshOkSaga';
import countyLoginSaga from './countyLoginSaga';
import countyPollSaga from './countyPollSaga';
import debugSaga from './debugSaga';
import dosLoginSaga from './dosLoginSaga';
import dosPollSaga from './dosPollSaga';


export default function* rootSaga() {
    yield all([
        countyLoginSaga(),
        countyPollSaga(),
        countyDashboardRefreshOkSaga(),
        debugSaga(),
        dosLoginSaga(),
        dosPollSaga(),
    ]);
}
