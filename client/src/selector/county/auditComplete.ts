function auditComplete(state: any): boolean {
    const { currentState } = state.county.asm.county;

    return currentState === 'COUNTY_AUDIT_COMPLETE';
}


export default auditComplete;
