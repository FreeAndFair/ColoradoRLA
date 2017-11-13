function canRenderReport(state: County.AppState): boolean {
    const asmState = state.asm.county;

    return asmState === 'COUNTY_AUDIT_UNDERWAY'
        || asmState === 'COUNTY_AUDIT_COMPLETE';
}


export default canRenderReport;
