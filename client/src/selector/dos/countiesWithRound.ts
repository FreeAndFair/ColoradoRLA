import * as _ from 'lodash';

import auditStarted from './auditStarted';


function countiesWithRound(state: DOS.AppState, round: number): DOS.CountyStatus[] {
    if (!auditStarted(state)) {
        return [];
    }

    const { countyStatus } = state;

    if (!countyStatus) {
        return [];
    }

    const statuses = _.values(countyStatus);

    return _.filter(statuses, c => {
        if (!c.rounds) { return false; }
        if (c.rounds.length < round) { return false; }

        return true;
    });
}


export default countiesWithRound;
