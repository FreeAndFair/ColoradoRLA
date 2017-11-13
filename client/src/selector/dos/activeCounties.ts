import * as _ from 'lodash';

import auditStarted from './auditStarted';


function activeCounties(state: DOS.AppState): DOS.CountyStatus[] {
    if (!auditStarted(state)) {
        return [];
    }

    const { countyStatus } = state;

    if (!countyStatus) {
        return [];
    }

    const deadlineMissed = (c: DOS.CountyStatus) => c.asmState === 'DEADLINE_MISSED';
    const complete = (c: DOS.CountyStatus) => c.asmState === 'COUNTY_AUDIT_COMPLETE';

    const statuses = _.values(countyStatus);
    return _.reject(statuses, c => complete(c) || deadlineMissed(c));
}


export default activeCounties;
