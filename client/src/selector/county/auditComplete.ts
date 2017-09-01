import * as _ from 'lodash';


function auditComplete(state: any): boolean {
    const { county } = state;
    const { currentState } = county.asm.county;

    if (currentState === 'COUNTY_AUDIT_COMPLETE') {
        return true;
    }

    if (!county.rounds || _.isEmpty(county.rounds)) {
        return false;
    }

    return county.estimatedBallotsToAudit <= 0;
}


export default auditComplete;
