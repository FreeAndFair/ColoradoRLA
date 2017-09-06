function currentRound(state: any): number {
    const { county } = state;

    if (!county.currentRound) {
        return null;
    }

    return county.currentRound.number;
}


export default currentRound;
