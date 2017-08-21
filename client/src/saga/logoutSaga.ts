import { takeLatest } from 'redux-saga/effects';


function* logout(): IterableIterator<void> {
    window.location.replace('/login');
}


export default function* logoutSaga() {
    yield takeLatest('LOGOUT', logout);
}
