import { all, takeEvery } from 'redux-saga/effects';

import dosDashboardRefresh from '../action/dosDashboardRefresh';


function* dosLoginSaga() {
    yield takeEvery('DOS_LOGIN_OK', () => {
        dosDashboardRefresh();
    });
}


export default function* rootSaga() {
    yield all([
        dosLoginSaga(),
    ]);
}
