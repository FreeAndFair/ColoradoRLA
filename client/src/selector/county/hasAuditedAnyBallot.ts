import * as _ from 'lodash';


function hasAuditedAnyBallot(state: AppState): boolean {
    const auditedBallotCount = _.get(state, 'county.auditedBallotCount');

    if (_.isNil(auditedBallotCount)) {
        return false;
    }

    return auditedBallotCount > 0;
}


export default hasAuditedAnyBallot;
