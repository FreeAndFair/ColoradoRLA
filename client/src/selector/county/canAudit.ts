import auditBoardSignedIn from './auditBoardSignedIn';


function canAudit(state: AppState) {
    if (!state.county) { return false; }
    if (!state.county.asm) { return false; }
    if (!state.county.asm.county) { return false; }

    const { currentState } = state.county.asm.county;

    if (!currentState) { return false; }

    return auditBoardSignedIn(state)
        && currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canAudit;
