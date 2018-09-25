import * as _ from 'lodash';

import isValidAuditBoard from './isValidAuditBoard';


const SIGNED_IN_STATES = [
    'WAITING_FOR_ROUND_START',
    'ROUND_IN_PROGRESS',
    'WAITING_FOR_ROUND_SIGN_OFF',
];

function auditBoardSignedIn(auditBoardIndex: number,
                            state: County.AppState): boolean {
    return _.has(state.auditBoards, auditBoardIndex)
        && isValidAuditBoard(state.auditBoards[auditBoardIndex].members)
        && _.includes(SIGNED_IN_STATES, state.asm.auditBoard);
}


export default auditBoardSignedIn;
