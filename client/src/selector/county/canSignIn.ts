function canSignIn(state: any) {
    const { county } = state;
    const { asm } = county;

    return asm.county.currentState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canSignIn;
