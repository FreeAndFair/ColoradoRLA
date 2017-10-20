function canSignIn(state: County.AppState): boolean {
    if (!state.asm) { return false; }
    if (!state.asm.county) { return false; }
    if (!state.asm.county.currentState) { return false; }

    const { currentState } = state.asm.county;

    return currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canSignIn;
