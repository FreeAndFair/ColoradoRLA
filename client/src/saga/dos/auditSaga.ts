import { takeLatest } from 'redux-saga/effects';

import notice from 'corla/notice';


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

function* setElectionInfoOk(): IterableIterator<void> {
    notice.ok('Election date and type are now set.');
}

function* setElectionInfoFail(): IterableIterator<void> {
    notice.danger('Unable to set election date and type.');
}

function* setElectionInfoNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to set election date and type: network failure.');
}

function* uploadRandomSeedFail(): IterableIterator<void> {
    notice.danger('Unable to set random number generator seed.');
    notice.danger('Please verify that the seed is a numeral at least 20 digits long.');
}

function* uploadRandomSeedNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to set random number generator seed: network failure.');
}

function* uploadRandomSeedOk(): IterableIterator<void> {
    notice.ok('Random number generator seed is now set.');
}

function* publishBallotsToAuditFail(): IterableIterator<void> {
    notice.danger('Unable to publish ballots to audit.');
}

function* publishBallotsToAuditNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to publish ballots to audit: network failure.');
}

function* publishBallotsToAuditOk(): IterableIterator<void> {
    notice.ok('Ballots to audit are now published.');
    notice.ok('The audit has started!');
}


export default function* dosLoginSaga() {
    yield takeLatest('SELECT_CONTESTS_FOR_AUDIT_FAIL', selectContestsForAuditFail);
    yield takeLatest('SELECT_CONTESTS_FOR_AUDIT_NETWORK_FAIL', selectContestsForAuditNetworkFail);
    yield takeLatest('SELECT_CONTESTS_FOR_AUDIT_OK', selectContestsForAuditOk);

    yield takeLatest('SET_ELECTION_INFO_FAIL', setElectionInfoFail);
    yield takeLatest('SET_ELECTION_INFO_NETWORK_FAIL', setElectionInfoNetworkFail);
    yield takeLatest('SET_ELECTION_INFO_OK', setElectionInfoOk);

    yield takeLatest('SET_RISK_LIMIT_FAIL', setRiskLimitFail);
    yield takeLatest('SET_RISK_LIMIT_NETWORK_FAIL', setRiskLimitNetworkFail);
    yield takeLatest('SET_RISK_LIMIT_OK', setRiskLimitOk);

    yield takeLatest('UPLOAD_RANDOM_SEED_FAIL', uploadRandomSeedFail);
    yield takeLatest('UPLOAD_RANDOM_SEED_NETWORK_FAIL', uploadRandomSeedNetworkFail);
    yield takeLatest('UPLOAD_RANDOM_SEED_OK', uploadRandomSeedOk);

    yield takeLatest('PUBLISH_BALLOTS_TO_AUDIT_FAIL', publishBallotsToAuditFail);
    yield takeLatest('PUBLISH_BALLOTS_TO_AUDIT_NETWORK_FAIL', publishBallotsToAuditNetworkFail);
    yield takeLatest('PUBLISH_BALLOTS_TO_AUDIT_OK', publishBallotsToAuditOk);
}
