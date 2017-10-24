import * as _ from 'lodash';


function allRoundsComplete(state: County.AppState): boolean {
    if (!state.asm) { return false; }
    if (!state.asm.county) { return false; }
    const { currentState } = state.asm.county;
    if (!currentState) { return false; }

    if (!state.rounds) { return false; }
    if (_.isEmpty(state.rounds)) { return false; }

    const { currentRound } = state;

    return !currentRound || _.isEmpty(currentRound);
}


export default allRoundsComplete;
