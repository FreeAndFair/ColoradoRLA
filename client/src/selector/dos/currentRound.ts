import * as _ from 'lodash';

import activeCounties from './activeCounties';
import auditStarted from './auditStarted';


function currentRound(state: any): number {
    const counties = activeCounties(state);

    let round = 0;

    for (const c of counties) {
        if (c.ballotsRemainingInRound > 0) {
            if (c.currentRound) {
                round = Math.max(round, c.currentRound.number);
            }
        }
    }

    return round;
}


export default currentRound;
