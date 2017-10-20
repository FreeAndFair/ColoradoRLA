function currentRoundNumber(state: County.AppState): Option<number> {
    if (!state.currentRound) { return null; }

    return state.currentRound.number;
}


export default currentRoundNumber;
