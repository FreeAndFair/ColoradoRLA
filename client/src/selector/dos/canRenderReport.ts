import * as _ from 'lodash';


const REPORT_STATES = [
    'AUDIT_READY_TO_START',
    'DOS_AUDIT_ONGOING',
    'DOS_ROUND_COMPLETE',
    'DOS_AUDIT_COMPLETE',
    'AUDIT_RESULTS_PUBLISHED',
];

function canRenderReport(state: DOS.AppState): boolean {
    return _.includes(REPORT_STATES, state.asm);
}


export default canRenderReport;
