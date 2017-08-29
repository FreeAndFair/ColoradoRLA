import isValidAuditBoard from './isValidAuditBoard';


function auditBoardSignedIn(state: any) {
    const { county } = state;

    if (!county) { return false; }

    const { auditBoard } = county;

    if (!auditBoard) { return false; }

    return isValidAuditBoard(auditBoard);
}


export default auditBoardSignedIn;
