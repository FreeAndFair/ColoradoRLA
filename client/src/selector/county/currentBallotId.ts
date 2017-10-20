import * as _ from 'lodash';


function currentBallotId(state: County.AppState): Option<number> {
    const { cvrsToAudit } = state;

    const currentCvr = _.find(cvrsToAudit, (cvr: any) => !cvr.audited);

    if (!currentCvr) { return null; }

    return currentCvr.db_id;
}


export default currentBallotId;
