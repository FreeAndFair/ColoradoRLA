function auditComplete(state: County.AppState): boolean {
    return state.asm.county === 'COUNTY_AUDIT_COMPLETE';
}


export default auditComplete;
