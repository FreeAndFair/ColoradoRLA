import * as _ from 'lodash';


function allRoundsComplete(state: AppState): boolean {
    const { county } = state;
    const { currentState } = county.asm.county;

    if (!county.rounds || _.isEmpty(county.rounds)) {
        return false;
    }

    const { currentRound } = county;

    return !currentRound || _.isEmpty(currentRound);
}


export default allRoundsComplete;
