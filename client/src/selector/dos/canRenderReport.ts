import * as _ from 'lodash';


const reportStates = [
    'AUDIT_READY_TO_START',
    'DOS_AUDIT_ONGOING',
    'DOS_AUDIT_COMPLETE',
    'AUDIT_RESULTS_PUBLISHED',
];

function canRenderReport(state: any) {
    const { currentState } = state.sos.asm;

    return _.includes(reportStates, currentState);
}


export default canRenderReport;
