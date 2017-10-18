function currentRoundNumber(state: AppState): Option<number> {
    const { county } = state;

    if (!county) { return null; }
    if (!county.currentRound) { return null; }

    return county.currentRound.number;
}


export default currentRoundNumber;
