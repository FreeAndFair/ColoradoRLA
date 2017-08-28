function canAudit(state: any) {
    const { county } = state;
    const { asm } = county;

    return asm.auditBoard.currentState === 'AUDIT_IN_PROGRESS'
        && asm.county.currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canAudit;
