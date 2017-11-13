import * as _ from 'lodash';


function missedDeadline(state: County.AppState): boolean {
    return state.asm.county === 'DEADLINE_MISSED';
}


export default missedDeadline;
