function canRenderReport(state: any) {
    const { currentState } = state.county.asm.county;

    if (!currentState) { return false; }

    return currentState === 'COUNTY_AUDIT_UNDERWAY'
        || currentState === 'COUNTY_AUDIT_COMPLETE';
}


export default canRenderReport;
