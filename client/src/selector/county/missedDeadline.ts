import * as _ from 'lodash';


function missedDeadline(state: AppState): boolean {
    const currentState = _.get(state, 'county.asm.county.currentState');
    if (_.isNil(currentState)) {
        return false;
    }

    return currentState === 'DEADLINE_MISSED';
}


export default missedDeadline;
