function previousRound(state: AppState): Round {
    const { county } = state;

    if (!county.rounds) {
        return null;
    }

    return county.rounds[county.rounds.length - 1];
}


export default previousRound;
