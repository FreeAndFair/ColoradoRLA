import auditBoardSignedIn from './auditBoardSignedIn';


function canAudit(state: County.AppState) {
    if (!state.asm) { return false; }
    if (!state.asm.county) { return false; }

    const { currentState } = state.asm.county;

    if (!currentState) { return false; }

    return auditBoardSignedIn(state)
        && currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canAudit;
