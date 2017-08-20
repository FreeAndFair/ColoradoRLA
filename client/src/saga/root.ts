import { all } from 'redux-saga/effects';

import countyLoginSaga from './countyLoginSaga';
import debugSaga from './debugSaga';
import dosLoginSaga from './dosLoginSaga';
import dosPollSaga from './dosPollSaga';


export default function* rootSaga() {
    yield all([
        countyLoginSaga(),
        debugSaga(),
        dosLoginSaga(),
        dosPollSaga(),
    ]);
}
