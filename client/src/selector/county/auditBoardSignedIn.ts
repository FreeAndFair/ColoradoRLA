import * as _ from 'lodash';

import isValidAuditBoard from './isValidAuditBoard';


const SIGNED_IN_STATES = [
    'WAITING_FOR_ROUND_START',
    'ROUND_IN_PROGRESS',
    'WAITING_FOR_ROUND_SIGN_OFF',
];

function auditBoardSignedIn(state: County.AppState): boolean {
    const { auditBoard } = state;
    if (!auditBoard) { return false; }

    if (!state.asm) { return false; }
    if (!state.asm.auditBoard) { return false; }
    const { currentState } = state.asm.auditBoard;
    if (!currentState) { return false; }

    return isValidAuditBoard(auditBoard)
        && _.includes(SIGNED_IN_STATES, currentState);
}


export default auditBoardSignedIn;
