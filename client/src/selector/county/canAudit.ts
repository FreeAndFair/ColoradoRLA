import auditBoardSignedIn from './auditBoardSignedIn';


function canAudit(state: County.AppState) {
    return auditBoardSignedIn(state)
        && state.asm.county === 'COUNTY_AUDIT_UNDERWAY';
}


export default canAudit;
