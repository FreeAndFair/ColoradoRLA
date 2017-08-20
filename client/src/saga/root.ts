import { all } from 'redux-saga/effects';

import debugSaga from './debugSaga';
import dosLoginSaga from './dosLoginSaga';
import dosPollSaga from './dosPollSaga';


export default function* rootSaga() {
    yield all([
        debugSaga(),
        dosLoginSaga(),
        dosPollSaga(),
    ]);
}
