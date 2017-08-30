function canAudit(state: any) {
    const { county } = state;
    const { asm } = county;

    return (asm.auditBoard.currentState === 'AUDIT_IN_PROGRESS'
            || asm.auditBoard.currentState === 'WAITING_FOR_ROUND_SIGN_OFF')
        && asm.county.currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canAudit;
