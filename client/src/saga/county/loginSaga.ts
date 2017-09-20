import {
    put,
    select,
    takeLatest,
} from 'redux-saga/effects';

import notice from 'corla/notice';


function* countyLoginFail(): IterableIterator<void> {
    notice.danger('Invalid credentials.');
}

function* countyLoginNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to log in due to network error.');
}


export default function* countyLoginSaga() {
    yield takeLatest('COUNTY_LOGIN_FAIL', countyLoginFail);
    yield takeLatest('COUNTY_LOGIN_NETWORK_FAIL', countyLoginNetworkFail);
}
