import { takeLatest } from 'redux-saga/effects';

import session from 'corla/session';


function* logoutRedirect(): IterableIterator<void> {
    session.expire();
    window.location.replace('/login');
}


export default function* logoutSaga() {
    const REDIRECT_ACTIONS = [
        'LOGOUT',
        'NOT_AUTHORIZED',
    ];

    yield takeLatest(REDIRECT_ACTIONS, logoutRedirect);
}
