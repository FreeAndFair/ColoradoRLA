import { endpoint } from '../config';

import createFetchAction from './createFetchAction';


export default (round: any) => {
    const start = round.startAuditPrefixLength;
    const ballotCount = round.expectedCount;
    const params = `start=${start}&ballot_count=${ballotCount}`;

    const url = `${endpoint('cvr-to-audit-list')}?${params}`;

    const a = createFetchAction({
        failType: 'FETCH_CVRS_TO_AUDIT_FAIL',
        networkFailType: 'FETCH_CVRS_TO_AUDIT_NETWORK_FAIL',
        okType: 'FETCH_CVRS_TO_AUDIT_OK',
        sendType: 'FETCH_CVRS_TO_AUDIT_SEND',
        url,
    });

    return a();
};
