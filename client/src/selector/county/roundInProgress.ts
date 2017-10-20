function roundInProgress(state: County.AppState): boolean {
    return state.ballotsRemainingInRound !== 0;
}


export default roundInProgress;
