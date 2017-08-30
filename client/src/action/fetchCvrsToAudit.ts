import { endpoint } from '../config';

import createFetchAction from './createFetchAction';


const url = endpoint('cvr-to-audit-list');


export default createFetchAction({
    failType: 'FETCH_CVRS_TO_AUDIT_FAIL',
    networkFailType: 'FETCH_CVRS_TO_AUDIT_NETWORK_FAIL',
    okType: 'FETCH_CVRS_TO_AUDIT_OK',
    sendType: 'FETCH_CVRS_TO_AUDIT_SEND',
    url,
});
