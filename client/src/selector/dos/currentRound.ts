import * as _ from 'lodash';

import activeCounties from './activeCounties';
import auditStarted from './auditStarted';


function currentRound(state: DOS.AppState): number {
    const counties = activeCounties(state);

    let roundNumber = 0;

    for (const c of counties) {
        const mostRecentRound = c.rounds[c.rounds.length - 1];
        const mostRecentRoundNumber = mostRecentRound
                                    ? mostRecentRound.number
                                    : 0;

        roundNumber = Math.max(roundNumber, mostRecentRoundNumber);
    }

    return roundNumber;
}


export default currentRound;
