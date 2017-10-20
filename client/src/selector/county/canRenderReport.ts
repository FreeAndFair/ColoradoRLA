function canRenderReport(state: County.AppState): boolean {
    if (!state.asm) { return false; }
    if (!state.asm.county) { return false; }

    const { currentState } = state.asm.county;

    if (!currentState) { return false; }

    return currentState === 'COUNTY_AUDIT_UNDERWAY'
        || currentState === 'COUNTY_AUDIT_COMPLETE';
}


export default canRenderReport;
