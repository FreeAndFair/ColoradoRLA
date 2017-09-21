import * as _ from 'lodash';

import auditStarted from './auditStarted';


function countiesWithRound(state: any, round: number): any[] {
    const { sos } = state;
    const { countyStatus } = sos;

    if (!auditStarted(state)) {
        return [];
    }

    if (!countyStatus) {
        return [];
    }


    const f = (c: any) => {
        if (!c.rounds) { return false; }
        if (c.rounds.length < round) { return false; }

        return true;
    };

    return _.filter(countyStatus, f);
}


export default countiesWithRound;
