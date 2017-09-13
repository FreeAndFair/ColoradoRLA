import * as _ from 'lodash';


const AUDIT_STARTED_STATES = [
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
    'DEADLINE_MISSED',
];


function auditStarted(state: any): boolean {
    if (!_.has(state, 'county.asm.county.currentState')) {
        return false;
    }

    const { currentState } = state.county.asm.county;

    return _.includes(AUDIT_STARTED_STATES, currentState);
}


export default auditStarted;
