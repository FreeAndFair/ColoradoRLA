import { all } from 'redux-saga/effects';

import ballotNotFoundOkSaga from './ballotNotFoundOkSaga';
import countyDashboardRefreshOkSaga from './countyDashboardRefreshOkSaga';
import countyLoginSaga from './countyLoginSaga';
import countyPollSaga from './countyPollSaga';
import debugSaga from './debugSaga';
import dosLoginSaga from './dosLoginSaga';
import dosPollSaga from './dosPollSaga';
import uploadAcvrOkSaga from './uploadAcvrOkSaga';


export default function* rootSaga() {
    yield all([
        ballotNotFoundOkSaga(),
        countyLoginSaga(),
        countyPollSaga(),
        countyDashboardRefreshOkSaga(),
        debugSaga(),
        dosLoginSaga(),
        dosPollSaga(),
        uploadAcvrOkSaga(),
    ]);
}
