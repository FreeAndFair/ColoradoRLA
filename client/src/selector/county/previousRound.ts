function previousRound(state: any): any {
    const { county } = state;

    if (!county.rounds) {
        return {};
    }

    const [ round ] = county.rounds;

    return round || {};
}


export default previousRound;
