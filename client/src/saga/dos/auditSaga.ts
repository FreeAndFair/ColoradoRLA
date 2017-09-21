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

function* setAuditInfoOk(): IterableIterator<void> {
    notice.ok('Audit info is now set.');
}

function* setAuditInfoFail(action: any): IterableIterator<void> {
    const { data } = action;
    const { result } = data;

    notice.danger(`Unable to set audit info: ${result}`);
}

function* setAuditInfoNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to set audit info: network failure.');
}

function* setHandCountOk(action: any): IterableIterator<void> {
    notice.ok('Contest selected for hand count.');
}

function* setHandCountFail(action: any): IterableIterator<void> {
    const { data } = action;
    const { result } = data;

    notice.danger(`Unable to set contest for hand count: ${result}`);
}

function* setHandCountNetworkFail(): IterableIterator<void> {
    notice.danger('Unable to set contest for hand count: network failure.');
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

    yield takeLatest('SET_AUDIT_INFO_FAIL', setAuditInfoFail);
    yield takeLatest('SET_AUDIT_INFO_NETWORK_FAIL', setAuditInfoNetworkFail);
    yield takeLatest('SET_AUDIT_INFO_OK', setAuditInfoOk);

    yield takeLatest('SET_HAND_COUNT_FAIL', setHandCountFail);
    yield takeLatest('SET_HAND_COUNT_NETWORK_FAIL', setHandCountNetworkFail);
    yield takeLatest('SET_HAND_COUNT_OK', setHandCountOk);

    yield takeLatest('UPLOAD_RANDOM_SEED_FAIL', uploadRandomSeedFail);
    yield takeLatest('UPLOAD_RANDOM_SEED_NETWORK_FAIL', uploadRandomSeedNetworkFail);
    yield takeLatest('UPLOAD_RANDOM_SEED_OK', uploadRandomSeedOk);

    yield takeLatest('PUBLISH_BALLOTS_TO_AUDIT_FAIL', publishBallotsToAuditFail);
    yield takeLatest('PUBLISH_BALLOTS_TO_AUDIT_NETWORK_FAIL', publishBallotsToAuditNetworkFail);
    yield takeLatest('PUBLISH_BALLOTS_TO_AUDIT_OK', publishBallotsToAuditOk);
}
