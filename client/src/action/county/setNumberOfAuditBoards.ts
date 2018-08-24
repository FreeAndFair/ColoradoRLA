import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('set-audit-board-count');

const setAuditBoardCount = createSubmitAction({
    failType: 'SET_NUMBER_OF_AUDIT_BOARDS_FAIL',
    networkFailType: 'SET_NUMBER_OF_AUDIT_BOARDS_NETWORK_FAIL',
    okType: 'SET_NUMBER_OF_AUDIT_BOARDS_OK',
    sendType: 'SET_NUMBER_OF_AUDIT_BOARDS_SEND',
    url,
});

interface SetAuditBoardCountParams {
    auditBoardCount: number;
}

export default (params: SetAuditBoardCountParams) => {
  const req = { count: params.auditBoardCount };

  setAuditBoardCount(req);
}
