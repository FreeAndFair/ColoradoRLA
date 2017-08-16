import { Dispatch } from 'redux';

import { endpoint } from '../config';

import createSubmitAction from './createSubmitAction';

import { format } from '../adapter/establishBoard';


const url = endpoint('audit-board');

const establishAuditBoard = createSubmitAction({
    failType: 'ESABLISH_AUDIT_BOARD_FAIL',
    networkFailType: 'ESABLISH_AUDIT_BOARD_NETWORK_FAIL',
    okType: 'ESABLISH_AUDIT_BOARD_OK',
    sendType: 'ESABLISH_AUDIT_BOARD_SEND',
    url,
});


export default (board: any) => establishAuditBoard(format(board));
