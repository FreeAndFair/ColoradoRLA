import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('audit-board-sign-out');

const auditBoardSignOut = createSubmitAction({
    failType: 'AUDIT_BOARD_SIGN_OUT_FAIL',
    networkFailType: 'AUDIT_BOARD_SIGN_OUT_NETWORK_FAIL',
    okType: 'AUDIT_BOARD_SIGN_OUT_OK',
    sendType: 'AUDIT_BOARD_SIGN_OUT_SEND',
    url,
});


export default (index: number) => auditBoardSignOut(index);
