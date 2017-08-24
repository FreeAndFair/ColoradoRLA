import { takeLatest } from 'redux-saga/effects';

import notice from '../notice';


function* selectContestsForAuditOk(): IterableIterator<void> {
    notice.ok('Contests for audit are now selected.');
}

function* setRiskLimitOk(): IterableIterator<void> {
    notice.ok('Comparison audit risk limit is now set.');
}

function* setRiskLimitFail(): IterableIterator<void> {
    notice.danger('Unable to set risk limit.');
}

function* setRiskLimitNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to set risk limit: network failure.');
}

function* uploadRandomSeedOk(): IterableIterator<void> {
    notice.danger('Random number generator seed is now set.');
}


export default function* dosLoginSaga() {
    yield takeLatest('SELECT_CONTESTS_FOR_AUDIT_OK', selectContestsForAuditOk);

    yield takeLatest('SET_RISK_LIMIT_FAIL', setRiskLimitFail);
    yield takeLatest('SET_RISK_LIMIT_NETWORK_FAIL', setRiskLimitNetworkFail);
    yield takeLatest('SET_RISK_LIMIT_OK', setRiskLimitOk);

    yield takeLatest('UPLOAD_RANDOM_SEED_OK', uploadRandomSeedOk);
}
