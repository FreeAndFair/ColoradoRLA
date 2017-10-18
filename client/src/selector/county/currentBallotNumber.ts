import * as _ from 'lodash';


function currentBallotNumber(state: AppState): Option<number> {
    const { county } = state;

    if (!county) { return null; }

    const { cvrsToAudit, currentBallot } = county;

    if (!cvrsToAudit) { return null; }
    if (!currentBallot) { return null; }

    return 1 + _.findIndex(cvrsToAudit, cvr => cvr.db_id === currentBallot.id);
}


export default currentBallotNumber;
