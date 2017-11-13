import * as _ from 'lodash';


function currentBallotNumber(state: County.AppState): Option<number> {
    const { cvrsToAudit, currentBallot } = state;

    if (!cvrsToAudit) { return null; }
    if (!currentBallot) { return null; }

    return 1 + _.findIndex(cvrsToAudit, cvr => cvr.db_id === currentBallot.id);
}


export default currentBallotNumber;
