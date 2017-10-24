function previousRound(state: County.AppState): Option<Round> {
    if (!state.rounds) {
        return null;
    }

    return state.rounds[state.rounds.length - 1];
}


export default previousRound;
