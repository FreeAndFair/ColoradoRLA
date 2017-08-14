import { Dispatch } from 'redux';

import { apiHost } from '../config';

import createSubmitAction from './createSubmitAction';


const url = `http://${apiHost}/audit-board`;

const establishAuditBoard = createSubmitAction({
    failType: 'ESABLISH_AUDIT_BOARD_FAIL',
    networkFailType: 'ESABLISH_AUDIT_BOARD_NETWORK_FAIL',
    okType: 'ESABLISH_AUDIT_BOARD_OK',
    sendType: 'ESABLISH_AUDIT_BOARD_SEND',
    url,
});


export default establishAuditBoard;
