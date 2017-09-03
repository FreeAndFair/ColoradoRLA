import { endpoint } from 'corla/config';

import createFetchAction from 'corla/action/createFetchAction';


const url = endpoint('audit-board-asm-state');


export default createFetchAction({
    failType: 'FETCH_AUDIT_BOARD_ASM_STATE_FAIL',
    networkFailType: 'FETCH_AUDIT_BOARD_ASM_STATE_NETWORK_FAIL',
    okType: 'FETCH_AUDIT_BOARD_ASM_STATE_OK',
    sendType: 'FETCH_AUDIT_BOARD_ASM_STATE_SEND',
    url,
});
