import * as _ from 'lodash';

import activeCounties from './activeCounties';
import auditStarted from './auditStarted';


function canStartNextRound(state: any): boolean {
    const counties = activeCounties(state);

    if (_.isEmpty(counties)) {
        return false;
    }

    const roundOngoing = (c: any) => c.ballotsRemainingInRound > 0;
    const allRoundsDone = !_.some(counties, roundOngoing);

    return allRoundsDone;
}


export default canStartNextRound;
