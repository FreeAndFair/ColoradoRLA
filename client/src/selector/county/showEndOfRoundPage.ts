import ballotsRemainingInRound from './ballotsRemainingInRound';
import roundInProgress from './roundInProgress';


function showEndOfRoundPage(state: any): boolean {
    return ballotsRemainingInRound(state) === 0
        && !roundInProgress(state);
}


export default showEndOfRoundPage;
