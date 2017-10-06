import { takeLatest } from 'redux-saga/effects';

import session from 'corla/session';


function* logoutRedirect(): IterableIterator<void> {
    session.expire();

    if (window.location.pathname !== '/login') {
        window.location.replace('/login');
    }
}


export default function* logoutSaga() {
    const REDIRECT_ACTIONS = [
        'LOGOUT',
        'NOT_AUTHORIZED',
        'RESET_DATABASE_OK',
    ];

    yield takeLatest(REDIRECT_ACTIONS, logoutRedirect);
}
