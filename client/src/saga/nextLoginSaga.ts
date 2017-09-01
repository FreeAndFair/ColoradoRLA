import {
    put,
    takeLatest,
} from 'redux-saga/effects';

import notice from '../notice';


function* loginOk(action: any) {
    const { role } = action.data.received;

    if (role === 'STATE') {
        yield put({ type: 'DOS_LOGIN_OK' });
    } else {
        yield put({ type: 'COUNTY_LOGIN_OK' });
    }
}

function* loginFail(): IterableIterator<void> {
    notice.danger('Invalid credentials.');
}

function* loginNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to log in due to network error.');
}


export default function* dosLoginSaga() {
    yield takeLatest('LOGIN_1F_NETWORK_FAIL', loginNetworkFail);
    yield takeLatest('LOGIN_1F_FAIL', loginFail);

    yield takeLatest('LOGIN_2F_FAIL', loginFail);
    yield takeLatest('LOGIN_2F_NETWORK_FAIL', loginNetworkFail);
    yield takeLatest('LOGIN_2F_OK', loginOk);
}
