import { takeLatest } from 'redux-saga/effects';

import * as cookies from 'js-cookie';


function* clearSession(): IterableIterator<void> {
    cookies.remove('JSESSIONID');
}


export default function* sessionSaga() {
    const clearSessionActions = [
        'COUNTY_LOGIN_SEND',
        'DOS_LOGIN_SEND',
        'LOGOUT',
    ];
    yield takeLatest(clearSessionActions, clearSession);
}
