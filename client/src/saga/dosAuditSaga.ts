import { takeLatest } from 'redux-saga/effects';

import notice from '../notice';


function* selectContestsForAuditFail(): IterableIterator<void> {
    notice.danger('Unable to select contests for audit.');
    notice.danger('Please verify that you selected at least one contest for audit.');
}

function* selectContestsForAuditNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to select contests for audit: network failure.');
}

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
    notice.ok('Random number generator seed is now set.');
}


export default function* dosLoginSaga() {
    yield takeLatest('SELECT_CONTESTS_FOR_AUDIT_FAIL', selectContestsForAuditFail);
    yield takeLatest('SELECT_CONTESTS_FOR_AUDIT_NETWORK_FAIL', selectContestsForAuditNetworkFail);
    yield takeLatest('SELECT_CONTESTS_FOR_AUDIT_OK', selectContestsForAuditOk);

    yield takeLatest('SET_RISK_LIMIT_FAIL', setRiskLimitFail);
    yield takeLatest('SET_RISK_LIMIT_NETWORK_FAIL', setRiskLimitNetworkFail);
    yield takeLatest('SET_RISK_LIMIT_OK', setRiskLimitOk);

    yield takeLatest('UPLOAD_RANDOM_SEED_OK', uploadRandomSeedOk);
}
