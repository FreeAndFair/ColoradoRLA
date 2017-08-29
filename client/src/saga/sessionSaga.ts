import { select, takeLatest } from 'redux-saga/effects';

import * as cookies from 'js-cookie';


function* checkSession(): IterableIterator<any> {
    const { session } = yield select();

    if (session) {
        const currentSession = cookies.get('JSESSIONID');

        if (session !== currentSession) {
            window.location.replace('/login');
        }
    }
}

function* clearSession(): IterableIterator<void> {
    cookies.remove('JSESSIONID');
}


export default function* sessionSaga() {
    const anyButLogout = (a: any) => a.type !== 'LOGOUT';
    yield takeLatest(anyButLogout, checkSession);

    const clearSessionActions = [
        'COUNTY_LOGIN_SEND',
        'DOS_LOGIN_SEND',
        'LOGOUT',
    ];
    yield takeLatest(clearSessionActions, clearSession);
}
