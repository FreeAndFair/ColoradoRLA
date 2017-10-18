import * as _ from 'lodash';


function currentBallotId(state: AppState): Option<number> {
    if (!state.county) { return null; }
    if (!state.county.cvrsToAudit) { return null; }

    const { cvrsToAudit } = state.county;

    const currentCvr = _.find(cvrsToAudit, (cvr: any) => !cvr.audited);

    if (!currentCvr) { return null; }

    return currentCvr.db_id;
}


export default currentBallotId;
