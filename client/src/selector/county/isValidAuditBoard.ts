function isValidElector(elector: any) {
    return elector.firstName
        && elector.firstName.trim()
        && elector.lastName
        && elector.lastName.trim()
        && elector.party;
}

function isValidAuditBoard(auditBoard: any) {
    if (!auditBoard[0]) { return false; }
    if (!auditBoard[1]) { return false; }

    return isValidElector(auditBoard[0])
        && isValidElector(auditBoard[1]) ;
}


export default isValidAuditBoard;
