import * as _ from 'lodash';

import {
    select,
    takeLatest,
} from 'redux-saga/effects';

import countyFetchCvr from 'corla/action/county/fetchCvr';

import currentBallotIdSelector from 'corla/selector/county/currentBallotId';


function* fetchCvrsToAuditOk(action: any): IterableIterator<any> {
    const { data } = action;
    const state = yield select();

    const currentId = currentBallotIdSelector(state);

    if (currentId) {
        countyFetchCvr(currentId);
    }
}

export default function* dosLoginSaga() {
    yield takeLatest('FETCH_CVRS_TO_AUDIT_OK', fetchCvrsToAuditOk);
}
