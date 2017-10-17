function auditComplete(state: AppState): boolean {
    const { county } = state;
    const { currentState } = county.asm.county;

    return currentState === 'COUNTY_AUDIT_COMPLETE';
}


export default auditComplete;
