import {
    put,
    takeEvery,
} from 'redux-saga/effects';

import dosDashboardRefresh from '../action/dosDashboardRefresh';
import dosFetchContests from '../action/dosFetchContests';


function* dosLoginOk() {
    dosDashboardRefresh();
    dosFetchContests();

    yield put({ type: 'DOS_POLL' });
}

export default function* dosLoginSaga() {
    yield takeEvery('DOS_LOGIN_OK', dosLoginOk);
}
