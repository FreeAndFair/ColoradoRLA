import { endpoint } from 'corla/config';

import createFetchAction from 'corla/action/createFetchAction';


export default (round: any) => {
    const params = `round=${round.number}&include_audited`;
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
