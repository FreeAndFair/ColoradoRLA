import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';

import { format } from '../adapter/establishBoard';


const url = endpoint('audit-board');

const establishAuditBoard = createSubmitAction({
    failType: 'ESTABLISH_AUDIT_BOARD_FAIL',
    networkFailType: 'ESTABLISH_AUDIT_BOARD_NETWORK_FAIL',
    okType: 'ESTABLISH_AUDIT_BOARD_OK',
    sendType: 'ESTABLISH_AUDIT_BOARD_SEND',
    url,
});


export default (board: any) => establishAuditBoard(format(board));
