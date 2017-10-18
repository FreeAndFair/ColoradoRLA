import * as _ from 'lodash';

import isValidAuditBoard from './isValidAuditBoard';


const SIGNED_IN_STATES = [
    'WAITING_FOR_ROUND_START',
    'ROUND_IN_PROGRESS',
    'WAITING_FOR_ROUND_SIGN_OFF',
];

function auditBoardSignedIn(state: AppState): boolean {
    if (!state.county) { return false; }
    if (!state.county.auditBoard) { return false; }
    if (!state.county.asm) { return false; }
    if (!state.county.asm.auditBoard) { return false; }

    const { auditBoard } = state.county;
    if (!auditBoard) { return false; }

    const { currentState } = state.county.asm.auditBoard;
    if (!currentState) { return false; }

    return isValidAuditBoard(auditBoard)
        && _.includes(SIGNED_IN_STATES, currentState);
}


export default auditBoardSignedIn;
