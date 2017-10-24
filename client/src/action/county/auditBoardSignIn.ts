import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';

import { format } from 'corla/adapter/establishBoard';


const url = endpoint('audit-board-sign-in');

const auditBoardSignIn = createSubmitAction({
    failType: 'AUDIT_BOARD_SIGN_IN_FAIL',
    networkFailType: 'AUDIT_BOARD_SIGN_IN_NETWORK_FAIL',
    okType: 'AUDIT_BOARD_SIGN_IN_OK',
    sendType: 'AUDIT_BOARD_SIGN_IN_SEND',
    url,
});


export default (board: AuditBoard) => auditBoardSignIn(format(board));
