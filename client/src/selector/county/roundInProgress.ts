import { isEmpty } from 'lodash';


function roundInProgress(state: any): boolean {
    const { county } = state;

    return !isEmpty(county.currentRound);
}


export default roundInProgress;
