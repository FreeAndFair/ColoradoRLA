function ballotsRemainingInRound(state: AppState): Option<number> {
    const { county } = state;

    if (!county) { return null; }

    return county.ballotsRemainingInRound;
}


export default ballotsRemainingInRound;
