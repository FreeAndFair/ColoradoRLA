import { endpoint } from 'corla/config';

import createSubmitAction from 'corla/action/createSubmitAction';


const url = endpoint('sign-off-audit-round');

const roundSignOff = createSubmitAction({
    failType: 'SUBMIT_ROUND_SIGN_OFF_FAIL',
    networkFailType: 'SUBMIT_ROUND_SIGN_OFF_NETWORK_FAIL',
    okType: 'SUBMIT_ROUND_SIGN_OFF_OK',
    sendType: 'SUBMIT_ROUND_SIGN_OFF_SEND',
    url,
});


function format(auditBoardIndex: number, electors: Elector[]): object {
    return {
        "audit_board_index": auditBoardIndex,
        "signatories": electors.map(e => ({
            first_name: e.firstName,
            last_name: e.lastName,
        })),
    };
}


export default (auditBoardIndex: number, electors: Elector[]) =>
    roundSignOff(format(auditBoardIndex, electors));
