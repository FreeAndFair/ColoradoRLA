function auditComplete(state: any): boolean {
    const { county } = state;
    const { currentState } = county.asm.county;

    return currentState === 'COUNTY_AUDIT_COMPLETE'
        || county.estimatedBallotsToAudit <= 0;
}


export default auditComplete;
