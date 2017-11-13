import {
    put,
    takeLatest,
} from 'redux-saga/effects';

import notice from 'corla/notice';
import session from 'corla/session';


function* login1FOk(action: any) {
    const { data } = action;
    const { stage } = data.received;

    if (stage === 'SECOND_FACTOR_AUTHENTICATED') {
        yield put({ type: 'LOGIN_2F_OK', data });
    }
}

function* login2FOk(action: any) {
    const { role } = action.data.received;

    if (role === 'STATE') {
        session.save({ type: 'dos' });
        yield put({ type: 'DOS_LOGIN_OK' });
    } else {
        session.save({ type: 'county' });
        yield put({ type: 'COUNTY_LOGIN_OK' });
    }
}

function* loginFail(): IterableIterator<void> {
    notice.danger('Invalid credentials.');
}

function* loginNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to log in due to network error.');
}


export default function* loginSaga() {
    yield takeLatest('LOGIN_1F_NETWORK_FAIL', loginNetworkFail);
    yield takeLatest('LOGIN_1F_FAIL', loginFail);
    yield takeLatest('LOGIN_1F_OK', login1FOk);

    yield takeLatest('LOGIN_2F_FAIL', loginFail);
    yield takeLatest('LOGIN_2F_NETWORK_FAIL', loginNetworkFail);
    yield takeLatest('LOGIN_2F_OK', login2FOk);
}
