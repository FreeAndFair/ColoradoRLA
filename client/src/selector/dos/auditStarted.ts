import * as _ from 'lodash';


const STARTED_STATES = [
    'BALLOT_ORDER_DEFINED',
    'AUDIT_READY_TO_START',
    'DOS_AUDIT_ONGOING',
    'DOS_ROUND_COMPLETE',
    'DOS_AUDIT_COMPLETE',
    'AUDIT_RESULTS_PUBLISHED',
];

function auditStarted(state: DOS.AppState): boolean {
    if (!state.asm) {
        return false;
    }

    const { currentState } = state.asm;

    return _.includes(STARTED_STATES, currentState);
}


export default auditStarted;
