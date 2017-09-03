import { endpoint } from 'corla/config';

import createFetchAction from './createFetchAction';


const url = endpoint('dos-asm-state');


export default createFetchAction({
    failType: 'FETCH_DOS_ASM_STATE_FAIL',
    networkFailType: 'FETCH_DOS_ASM_STATE_NETWORK_FAIL',
    okType: 'FETCH_DOS_ASM_STATE_OK',
    sendType: 'FETCH_DOS_ASM_STATE_SEND',
    url,
});
