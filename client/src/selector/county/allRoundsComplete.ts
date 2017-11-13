import * as _ from 'lodash';


function allRoundsComplete(state: County.AppState): boolean {
    if (_.isEmpty(state.rounds)) { return false; }

    const { currentRound } = state;

    return !currentRound || _.isEmpty(currentRound);
}


export default allRoundsComplete;
