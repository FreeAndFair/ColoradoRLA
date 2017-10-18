import { isEmpty } from 'lodash';


function roundInProgress(state: AppState): boolean {
    const { county } = state;

    if (!county) { return false; }

    return county.ballotsRemainingInRound !== 0;
}


export default roundInProgress;
