import * as _ from 'lodash';

import auditBoardSignedIn from './auditBoardSignedIn';


function canAudit(state: AppState) {
    if (!_.has(state, 'county.asm.county.currentState')) {
        return false;
    }

    const countyASMState = state.county.asm.county.currentState;

    return auditBoardSignedIn(state)
        && countyASMState === 'COUNTY_AUDIT_UNDERWAY';
}


export default canAudit;
