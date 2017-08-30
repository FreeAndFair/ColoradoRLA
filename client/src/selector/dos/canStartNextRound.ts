import * as _ from 'lodash';

import auditStarted from './auditStarted';


function canStartNextRound(state: any): boolean {
    const { sos } = state;
    const { countyStatus } = sos;

    if (!auditStarted(state)) {
        return false;
    }

    if (!countyStatus) {
        return false;
    }

    const deadlineMissed = (c: any) => c.asmState === 'DEADLINE_MISSED';
    const activeCounties = _.reject(countyStatus, deadlineMissed);

    if (_.isEmpty(activeCounties)) {
        return false;
    }

    const roundOngoing = (c: any) => c.ballotsRemainingInRound > 0;
    const allRoundsDone = !_.some(activeCounties, roundOngoing);

    return allRoundsDone;
}


export default canStartNextRound;
