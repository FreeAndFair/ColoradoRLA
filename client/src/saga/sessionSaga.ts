import { select, takeLatest } from 'redux-saga/effects';


function* checkSession(): IterableIterator<void> {
}

function* clearSession(): IterableIterator<void> {
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
