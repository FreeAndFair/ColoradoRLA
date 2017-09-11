import * as _ from 'lodash';

import activeCounties from './activeCounties';
import auditStarted from './auditStarted';


function canStartNextRound(state: any): boolean {
    const counties = activeCounties(state);

    if (_.isEmpty(counties)) {
        return false;
    }

    const waitingForRoundStart = (c: any) =>
        c.auditBoardAsmState === 'WAITING_FOR_ROUND_START';

    const allRoundsDone = _.every(counties, waitingForRoundStart);

    return allRoundsDone;
}


export default canStartNextRound;
