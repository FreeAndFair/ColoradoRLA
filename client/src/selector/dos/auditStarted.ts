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
    return _.includes(STARTED_STATES, state.asm);
}


export default auditStarted;
