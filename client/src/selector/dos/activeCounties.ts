import * as _ from 'lodash';

import auditStarted from './auditStarted';


function activeCounties(state: any): any[] {
    const { sos } = state;
    const { countyStatus } = sos;

    if (!auditStarted(state)) {
        return [];
    }

    if (!countyStatus) {
        return [];
    }

    const deadlineMissed = (c: any) => c.asmState === 'DEADLINE_MISSED';
    const complete = (c: any) => c.asmState === 'COUNTY_AUDIT_COMPLETE';

    return _.reject(countyStatus, (c: any) => complete(c) || deadlineMissed(c));
}


export default activeCounties;
