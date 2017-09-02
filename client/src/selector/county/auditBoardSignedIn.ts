import * as _ from 'lodash';

import isValidAuditBoard from './isValidAuditBoard';


const signedInStates = [
    'WAITING_FOR_ROUND_START',
    'ROUND_IN_PROGRESS',
    'WAITING_FOR_ROUND_SIGN_OFF',
];


function auditBoardSignedIn(state: any) {
    if (!_.has(state, 'county.auditBoard')) {
        return false;
    }

    if (!_.has(state, 'county.asm.auditBoard.currentState')) {
        return false;
    }

    const { auditBoard } = state.county;
    const { currentState } = state.county.asm.auditBoard;

    return isValidAuditBoard(auditBoard)
        && _.includes(signedInStates, currentState);
}


export default auditBoardSignedIn;
