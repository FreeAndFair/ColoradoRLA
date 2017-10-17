function currentRoundNumber(state: AppState): number {
    const { county } = state;

    if (!county.currentRound) {
        return null;
    }

    return county.currentRound.number;
}


export default currentRoundNumber;
