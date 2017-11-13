import * as _ from 'lodash';


const AUDIT_STARTED_STATES = [
    'COUNTY_AUDIT_UNDERWAY',
    'COUNTY_AUDIT_COMPLETE',
    'DEADLINE_MISSED',
];

function auditStarted(state: County.AppState): boolean {
    return _.includes(AUDIT_STARTED_STATES, state.asm.county);
}


export default auditStarted;
