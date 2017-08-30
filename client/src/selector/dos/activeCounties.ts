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
    return _.reject(countyStatus, deadlineMissed);
}


export default activeCounties;
