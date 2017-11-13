import * as _ from 'lodash';

import activeCounties from './activeCounties';
import auditStarted from './auditStarted';


function canStartNextRound(state: DOS.AppState): boolean {
    const counties = activeCounties(state);

    if (_.isEmpty(counties)) {
        return false;
    }

    const waitingForRoundStart = (c: DOS.CountyStatus) =>
        c.auditBoardASMState === 'WAITING_FOR_ROUND_START';

    const allRoundsDone = _.every(counties, waitingForRoundStart);

    return allRoundsDone;
}


export default canStartNextRound;
