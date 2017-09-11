import { takeLatest } from 'redux-saga/effects';


function* logoutRedirect(): IterableIterator<void> {
    window.location.replace('/login');
}


export default function* logoutSaga() {
    const REDIRECT_ACTIONS = [
        'LOGOUT',
        'NOT_AUTHORIZED',
    ];

    yield takeLatest(REDIRECT_ACTIONS, logoutRedirect);
}
