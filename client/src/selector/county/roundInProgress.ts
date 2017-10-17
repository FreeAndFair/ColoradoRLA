import { isEmpty } from 'lodash';


function roundInProgress(state: AppState): boolean {
    const { county } = state;

    return county.ballotsRemainingInRound !== 0;
}


export default roundInProgress;
