function ballotsRemainingInRound(state: any): number {
    const { county } = state;

    return county.ballotsRemainingInRound;
}


export default ballotsRemainingInRound;
