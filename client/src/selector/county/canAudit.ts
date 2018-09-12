import auditBoardSignedIn from './auditBoardSignedIn';


function canAudit(state: County.AppState) {
    return state.asm.county === 'COUNTY_AUDIT_UNDERWAY';

    // TODO: Do we need to test auditBoardSignedIn?
    /*
    return auditBoardSignedIn(state)
        && state.asm.county === 'COUNTY_AUDIT_UNDERWAY';
    */
}


export default canAudit;
