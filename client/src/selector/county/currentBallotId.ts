import * as _ from 'lodash';


function currentBallotId(state: AppState): number {
    if (!_.has(state, 'county.cvrsToAudit')) {
        return null;
    }

    const { cvrsToAudit } = state.county;

    const currentCvr = _.find(cvrsToAudit, (cvr: any) => !cvr.audited);

    return _.get(currentCvr, 'db_id');
}


export default currentBallotId;
