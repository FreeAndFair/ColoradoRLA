function canSignIn(state: AppState): boolean {
    if (!state.county) { return false; }
    if (!state.county.asm) { return false; }
    if (!state.county.asm.county) { return false; }
    if (!state.county.asm.county.currentState) { return false; }

    const { currentState } = state.county.asm.county;

    return currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canSignIn;
