import * as _ from 'lodash';


const REPORT_STATES = [
    'AUDIT_READY_TO_START',
    'DOS_AUDIT_ONGOING',
    'DOS_ROUND_COMPLETE',
    'DOS_AUDIT_COMPLETE',
    'AUDIT_RESULTS_PUBLISHED',
];

function canRenderReport(state: DOS.AppState): boolean {
    if (!state.asm) { return false; }

    const { currentState } = state.asm;

    if (!currentState) { return false; }

    return _.includes(REPORT_STATES, currentState);
}


export default canRenderReport;
