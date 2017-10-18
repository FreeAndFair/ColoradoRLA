function previousRound(state: AppState): Option<Round> {
    const { county } = state;

    if (!county) { return null; }

    if (!county.rounds) {
        return null;
    }

    return county.rounds[county.rounds.length - 1];
}


export default previousRound;
