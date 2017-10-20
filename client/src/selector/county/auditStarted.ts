import * as _ from 'lodash';


const AUDIT_STARTED_STATES = [
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
    'DEADLINE_MISSED',
];

function auditStarted(state: County.AppState): boolean {
    if (!state.asm) { return false; }
    if (!state.asm.county) { return false; }

    const { currentState } = state.asm.county;

    if (!currentState) { return false; }

    return _.includes(AUDIT_STARTED_STATES, currentState);
}


export default auditStarted;
