import * as _ from 'lodash';


function allRoundsComplete(state: any): boolean {
    const { county } = state;
    const { currentState } = county.asm.county;

    if (!county.rounds || _.isEmpty(county.rounds)) {
        return false;
    }

    return county.estimatedBallotsToAudit <= 0;
}


export default allRoundsComplete;
