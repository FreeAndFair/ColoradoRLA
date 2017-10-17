function ballotsRemainingInRound(state: AppState): number {
    const { county } = state;

    return county.ballotsRemainingInRound;
}


export default ballotsRemainingInRound;
