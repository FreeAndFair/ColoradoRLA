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

    const nextId = currentBallotIdSelector(state);

    if (!nextId) { return; }

    const { currentBallot } = state;

    if (!currentBallot || (currentBallot.id !== nextId)) {
        // If we already have a current ballot, only fetch this CVR if
        // it is new. Otherwise we already have it, and fetching it
        // again would overwrite the `submitted` flag, causing us to
        // forget that we are waiting for the submission to be handled.
        countyFetchCvr(nextId);
    }
}

export default function* dosLoginSaga() {
    yield takeLatest('FETCH_CVRS_TO_AUDIT_OK', fetchCvrsToAuditOk);
}
