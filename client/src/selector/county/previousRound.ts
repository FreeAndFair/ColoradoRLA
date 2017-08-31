function previousRound(state: any): any {
    const { county } = state;

    if (!county.rounds) {
        return {};
    }

    return county.rounds[county.rounds.length - 1];
}


export default previousRound;
