import * as _ from 'lodash';


const STARTED_STATES = [
    'AUDIT_READY_TO_START',
    'DOS_AUDIT_ONGOING',
    'DOS_AUDIT_COMPLETE',
    'AUDIT_RESULTS_PUBLISHED',
];

function auditStarted(state: any): boolean {
    const { currentState } = state.sos.asm;

    return _.includes(STARTED_STATES, currentState);
}


export default auditStarted;
