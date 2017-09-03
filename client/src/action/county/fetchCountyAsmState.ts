import { endpoint } from 'corla/config';

import createFetchAction from 'corla/action/createFetchAction';


const url = endpoint('county-asm-state');


export default createFetchAction({
    failType: 'FETCH_COUNTY_ASM_STATE_FAIL',
    networkFailType: 'FETCH_COUNTY_ASM_STATE_NETWORK_FAIL',
    okType: 'FETCH_COUNTY_ASM_STATE_OK',
    sendType: 'FETCH_COUNTY_ASM_STATE_SEND',
    url,
});
