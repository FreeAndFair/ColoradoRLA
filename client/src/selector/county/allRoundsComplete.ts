import * as _ from 'lodash';


function allRoundsComplete(state: AppState): boolean {
    if (!state.county) { return false; }
    if (!state.county.asm) { return false; }
    if (!state.county.asm.county) { return false; }

    const { currentState } = state.county.asm.county;

    if (!currentState) { return false; }

    if (!state.county.rounds) { return false; }
    if (_.isEmpty(state.county.rounds)) { return false; }

    const { currentRound } = state.county;

    return !currentRound || _.isEmpty(currentRound);
}


export default allRoundsComplete;
