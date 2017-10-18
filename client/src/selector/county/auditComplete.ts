function auditComplete(state: AppState): boolean {
    if (!state.county) { return false; }
    if (!state.county.asm) { return false; }
    if (!state.county.asm.county) { return false; }

    const { currentState } = state.county.asm.county;

    if (!currentState) { return false; }

    return currentState === 'COUNTY_AUDIT_COMPLETE';
}


export default auditComplete;
